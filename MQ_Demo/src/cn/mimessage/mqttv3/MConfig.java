/*
 * Name   MConfig.java
 * Author ZhangZhenli
 * Created on 2012-11-18, 上午11:46:36
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package cn.mimessage.mqttv3;

/**
 * 
 * @author ZhangZhenli
 */
public class MConfig implements PushConfig {

	private static final String TAG = "MConfig.java";

	/*
	 * @see cn.mimessage.mqttv3.PushConfig#getServerUrl()
	 */
	@Override
	public String getServerUrl() {
		String broker = "42.121.4.114";
		int port = 1883;
		return "tcp://" + broker + ":" + port;
	}

	/*
	 * @see cn.mimessage.mqttv3.PushConfig#getClientId()
	 */
	@Override
	public String getClientId() {
		return "10001";
	}
}
