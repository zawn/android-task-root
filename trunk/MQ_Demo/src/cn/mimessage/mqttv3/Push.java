/*
 * Name   Push.java
 * Author ZhangZhenli
 * Created on 2012-11-18, 下午12:02:24
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package cn.mimessage.mqttv3;


/**
 * 
 * @author ZhangZhenli
 */
public class Push extends PushService {

	private static final String TAG = "Push.java";

	@Override
	public PushCallback getPushCallback() {
		return new CallBack(getApplicationContext(), this.getClass());
	}

	@Override
	public PushConfig getPushConfig() {
		return new MConfig();
	}
	
	@Override
	public boolean isEnableBackgroundService() {
		return false;		
	}
}
