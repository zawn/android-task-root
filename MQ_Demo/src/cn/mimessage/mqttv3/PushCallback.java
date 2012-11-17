/*
 * Name   PushCallback.java
 * Author ZhangZhenli
 * Created on 2012-11-13, 上午11:25:45
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package cn.mimessage.mqttv3;

import java.sql.Timestamp;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

/**
 * 
 * @author ZhangZhenli
 */
public class PushCallback implements MqttCallback {

	private static final String TAG = "PushCallback.java";
	private Context mContext;
	private NotificationManager mNotifMan;
	private NotificationManager mNotificationManager;
	private static int mMessagesNumber;
	private static NotificationCompat.Builder mNotifyBuilder;
	private static final int notifyID = 1;
	// Notification title
	public static String NOTIF_TITLE = "MQTTv3";

	public PushCallback(Context context) {
		this.mContext = context;
		mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		// Sets an ID for the notification, so it can be updated

		mNotifyBuilder = new NotificationCompat.Builder(mContext).setSmallIcon(R.drawable.ic_launcher)
				.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL).setWhen(System.currentTimeMillis());
	}

	@Override
	public void connectionLost(Throwable cause) {
		// This method is called when the connection to the server is lost.
		Log.e(TAG, "connectionLost");
		cause.printStackTrace();
		// TODO:合理连接丢失重试机制
		final Intent service = new Intent(mContext, PushService.class);
		service.setAction(MqttIntent.CONNECT_LOST);
		mContext.startService(service);
	}

	@Override
	public void messageArrived(MqttTopic topic, MqttMessage message) throws Exception {
		// Called when a message arrives from the server.
		Log.i(TAG, "MessageArrived");
		String time = new Timestamp(System.currentTimeMillis()).toString();
		Log.i(TAG, "Time:\t" + time + "  Topic:\t" + topic.getName() + "  Message:\t"
				+ new String(message.getPayload()) + "  QoS:\t" + message.getQos());

		// New Message Arrived Handler
		PushMessage pushMessage = new PushMessage(topic.getName(), message);
		showNotification(pushMessage);
		Log.i(TAG, "MessageArrived End");
	}

	@Override
	public void deliveryComplete(MqttDeliveryToken token) {
		// Here use synchronous delivery, by using the token.waitForCompletion() call in the work thread.
	}

	// Display the topbar notification
	@SuppressWarnings("deprecation")
	private void showNotification(PushMessage message) {

		Intent intent = new Intent(mContext, MainActivity.class);
		intent.setAction(MqttIntent.MSGARRIVED);
		intent.putExtra(MqttIntent.MSG, message);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		// Simply open the parent activity
		PendingIntent pi = PendingIntent.getActivity(mContext, 0, intent, 0);
		
		
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
		// Adds the back stack
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent to the top of the stack
		stackBuilder.addNextIntent(intent);
		// Gets a PendingIntent containing the entire back stack
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mNotifyBuilder.setContentIntent(resultPendingIntent);

		// Start of a loop that processes data and then notifies the user
		mNotifyBuilder.setContentTitle(message.getTopicName());
		mNotifyBuilder.setContentText(message.getPayloadString());
		mNotifyBuilder.setContentIntent(resultPendingIntent);
		mNotifyBuilder.setNumber(++mMessagesNumber);
		mNotifyBuilder.setTicker(message.getPayloadString());
		mNotifyBuilder.setWhen(System.currentTimeMillis());
		// Because the ID remains unchanged, the existing notification is
		// updated.
		mNotificationManager.notify(notifyID, mNotifyBuilder.build());
	}
}
