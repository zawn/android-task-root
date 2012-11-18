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
import java.util.ArrayList;
import java.util.Collections;

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
import android.util.Log;
import cn.mimail.sdk.util.Utils;

/**
 * 
 * @author ZhangZhenli
 */
public abstract class PushCallback implements MqttCallback {

	private static final String TAG = "PushCallback.java";
	private Context mContext;
	private NotificationManager mNotificationManager;
	private Class<?> mServerClazz;
	private static volatile ArrayList<PushMessage> mMessages = new ArrayList<PushMessage>();
	private static Intent mContentIntent;
	private static PendingIntent mDeletePendingIntent;
	private static NotificationCompat.Builder mNotifyBuilder;
	private static final int notifyID = 1;

	/**
	 * 构造函数
	 * 
	 * @param context 应用程序上下文
	 * @param serverCls 具体的服务类
	 */
	public PushCallback(Context context, Class<?> serverCls) {
		this.mContext = context;
		mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		// Sets an ID for the notification, so it can be updated

		mNotifyBuilder = new NotificationCompat.Builder(mContext).setSmallIcon(R.drawable.ic_launcher)
				.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL).setWhen(System.currentTimeMillis());
		mServerClazz = serverCls;
		final ArrayList<PushMessage> temp = getMessage();
		if (temp != null) {
			mMessages.addAll(temp);
		}
	}

	@Override
	public void connectionLost(Throwable cause) {
		// This method is called when the connection to the server is lost.
		Log.e(TAG, "connectionLost");
		cause.printStackTrace();
		// TODO:合理连接丢失重试机制
		final Intent service = new Intent(mContext, mServerClazz);
		service.setAction(PushIntent.CONNECT_LOST);
		mContext.startService(service);
	}

	@Override
	public void messageArrived(MqttTopic topic, MqttMessage message) throws Exception {
		// Called when a message arrives from the server.
		Log.i(TAG, "MessageArrived Start");
		String time = new Timestamp(System.currentTimeMillis()).toString();
		Log.i(TAG, "Time:\t" + time + "  Topic:\t" + topic.getName() + "  Message:\t"
				+ new String(message.getPayload()) + "  QoS:\t" + message.getQos());

		// New Message Arrived Handler
		PushMessage pushMessage = new PushMessage(topic.getName(), message);
		if (Utils.isActivityForeground(mContext, getStartActivity())) {
			Log.i(TAG, "Utils.isActivityForeground(mContext, MainActivity.class)");
			mMessages.add(pushMessage);
			Intent intent = new Intent(mContext, getStartActivity());
			intent.setAction(PushIntent.MESSAGE_ARRIVED);
			intent.putExtra(PushIntent.MESSAGE, mMessages);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			mContext.startActivity(intent);
		} else {
			Log.i(TAG, "showNotification");
			showNotification(pushMessage);
		}
		Log.i(TAG, "MessageArrived End");
	}

	@Override
	public void deliveryComplete(MqttDeliveryToken token) {
		// Here use synchronous delivery, by using the token.waitForCompletion() call in the work thread.
	}

	// Display the topbar notification
	private void showNotification(PushMessage message) {
		mNotifyBuilder.setContentIntent(getContentIntent(message));
		// 不清除消息,防止消息丢失
		// mNotifyBuilder.setDeleteIntent(getDeleteIntent());
		// Start of a loop that processes data and then notifies the user
		mNotifyBuilder.setContentTitle(message.getTopicName());
		mNotifyBuilder.setContentText(message.getPayloadString());
		mNotifyBuilder.setNumber(mMessages.size());
		mNotifyBuilder.setTicker(message.getPayloadString());
		mNotifyBuilder.setWhen(System.currentTimeMillis());
		// Because the ID remains unchanged, the existing notification is
		// updated.
		mNotificationManager.notify(notifyID, mNotifyBuilder.build());
	}

	final private PendingIntent getContentIntent(PushMessage message) {
		if (mContentIntent == null) {
			mContentIntent = new Intent(mContext, getStartActivity());
			mContentIntent.setAction(PushIntent.MESSAGE_ARRIVED);
			mContentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContentIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		}
		mMessages.add(message);
		saveMessage(mMessages);
		mContentIntent.putExtra(PushIntent.MESSAGE, mMessages);
		// PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		return PendingIntent.getActivity(mContext, 0, mContentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	final private PendingIntent getDeleteIntent() {
		if (mDeletePendingIntent == null) {
			Intent deleteIntent = new Intent(mContext, mServerClazz);
			deleteIntent.setAction(PushIntent.NOTIFICATION_READ);
			deleteIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			deleteIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			mDeletePendingIntent = PendingIntent.getService(mContext, 0, deleteIntent, 0);
		}
		return mDeletePendingIntent;
	}

	public abstract Class<?> getStartActivity();

	/**
	 * 消息已经阅读后的回调函数
	 */
	public void notificationRead() {
		Log.i(TAG, "PushCallback.notificationRead()");
		mMessages.clear();
		clearMessage();
	}

	/**
	 * 保存未读消息
	 * 
	 * @param mMessages
	 */
	public void saveMessage(ArrayList<PushMessage> mMessages) {
	}

	/**
	 * 恢复未读消息
	 * 
	 * @param mMessages
	 */
	public ArrayList<PushMessage> getMessage() {
		return null;
	}

	/**
	 * 清除未读消息
	 * 
	 * @param mMessages
	 */
	public void clearMessage() {
	}
}
