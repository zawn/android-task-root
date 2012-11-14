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

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 
 * @author ZhangZhenli
 */
public class PushCallback implements MqttCallback {

	private static final String TAG = "PushCallback.java";
	private Context mContext;

	public PushCallback(Context context) {
		this.mContext = context;
	}

	@Override
	public void connectionLost(Throwable cause) {
		Log.e(TAG, "connectionLost");
	}

	@Override
	public void messageArrived(MqttTopic topic, MqttMessage message) throws Exception {
		Log.i(TAG, "MessageArrived");
		// Called when a message arrives from the server.
		String time = new Timestamp(System.currentTimeMillis()).toString();
		Log.i(TAG, "Time:\t" + time + "  Topic:\t" + topic.getName() + "  Message:\t"
				+ new String(message.getPayload()) + "  QoS:\t" + message.getQos());

		Intent intent = new Intent(mContext, MainActivity.class);
		intent.setAction(MqttIntent.MSGARRIVED);
		intent.putExtra(MqttIntent.MSG,
				new PushMessage(topic.getName(), new String(message.getPayload()), message.getQos()));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		mContext.startActivity(intent);
		Log.i(TAG, "MessageArrived End");
	}

	@Override
	public void deliveryComplete(MqttDeliveryToken token) {
		// Here use synchronous delivery, by using the token.waitForCompletion() call in the work thread.
	}

}
