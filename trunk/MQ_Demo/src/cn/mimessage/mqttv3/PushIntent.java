/*
 * Name   PushIntent.java
 * Author ZhangZhenli
 * Created on 2012-10-8, 下午7:55:41
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package cn.mimessage.mqttv3;

/**
 * 相关Intent常量
 * 
 * @author ZhangZhenli
 */
public class PushIntent {

	public static final String CONNECT 				= "cn.mimessage.mqttv3.CONNECT";
	public static final String RECONNECT 			= "cn.mimessage.mqttv3.RECONNECT";
	public static final String CONNECT_LOST 		= "cn.mimessage.mqttv3.CONNECT_LOST";
	public static final String CONNECT_CHANGE 		= "cn.mimessage.mqttv3.CONNECT_CHANGE";
	public static final String DISCONNECT           = "cn.mimessage.mqttv3.DISCONNECT";
	public static final String KEEPALIVE			= "cn.mimessage.mqttv3.KEEPALIVE";
	public static final String PUBLISH 				= "cn.mimessage.mqttv3.PUBLISH";
	public static final String SUBSCRIBE 			= "cn.mimessage.mqttv3.SUBSCRIBE";
	public static final String UNSUBSCRIBE 			= "cn.mimessage.mqttv3.UNSUBSCRIBE";
	public static final String MESSAGE 				= "cn.mimessage.mqttv3.MQTT_MESSAGE";
	public static final String MESSAGE_ARRIVED 		= "cn.mimessage.mqttv3.MQTT_MESSAGE_ARRIVED";
	public static final String MESSAGE_DELIVERED 	= "cn.mimessage.mqttv3.MQTT_MESSAGE_DELIVERED";
}
