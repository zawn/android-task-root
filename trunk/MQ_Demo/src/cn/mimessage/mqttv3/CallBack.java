/*
 * Name   CallBack.java
 * Author ZhangZhenli
 * Created on 2012-11-18, 下午1:32:40
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package cn.mimessage.mqttv3;

import java.util.ArrayList;

import android.content.Context;

/**
 * 
 * @author ZhangZhenli
 */
public class CallBack extends PushCallback {

	/**
	 * @param context
	 * @param serverCls
	 */
	public CallBack(Context context, Class<?> serverCls) {
		super(context, serverCls);
	}

	private static final String TAG = "CallBack.java";

	@Override
	public Class<?> getStartActivity() {
		return MainActivity.class;
	}

	/*
	 * @see cn.mimessage.mqttv3.PushCallback#saveMessage(java.util.ArrayList)
	 */
	@Override
	public void saveMessage(ArrayList<PushMessage> mMessages) {

	}

	/*
	 * @see cn.mimessage.mqttv3.PushCallback#getMessage()
	 */
	@Override
	public ArrayList<PushMessage> getMessage() {
		return null;
	}

	/*
	 * @see cn.mimessage.mqttv3.PushCallback#clearMessage()
	 */
	@Override
	public void clearMessage() {

	}
}
