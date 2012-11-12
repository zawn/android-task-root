/*
 * Name   MQConnectivityReceiver.java
 * Author ZhangZhenli
 * Created on 2012-11-7, 下午3:55:08
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package cn.mimessage.mqttv3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 
 * @author ZhangZhenli
 */
public class MQConnectivityReceiver extends BroadcastReceiver {

	private static final String TAG = "MQConnectivityReceiver.java";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (BuildConfig.DEBUG)
			Log.i(TAG, "Start onReceive");
		notifyContextService(context);
		if (BuildConfig.DEBUG)
			Log.i(TAG, "End   onReceive");
	}

	private void notifyContextService(final Context context) {
		if (BuildConfig.DEBUG)
			Log.i(TAG, "notifyContextService");
		final Intent service = new Intent(context, PushService.class);
		service.setAction(MqttIntent.CONNECT_CHANGE);
		context.startService(service);
	}
}
