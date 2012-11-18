/*
 * Name   Push.java
 * Author ZhangZhenli
 * Created on 2012-11-18, 下午12:02:24
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package cn.mimessage.mqttv3;

import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import android.util.Log;

/**
 * 
 * @author ZhangZhenli
 */
public class Push extends PushService {

	private static final String TAG = "Push.java";

	@Override
	public PushCallback getPushCallback() {
		return null;
	}

	@Override
	public PushConfig getPushConfig() {
		return new MConfig();
	}
}
