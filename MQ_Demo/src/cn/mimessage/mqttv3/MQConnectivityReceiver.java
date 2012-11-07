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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
import cn.mimessage.mqttv3.PushService.LocalBinder;

/**
 * 
 * @author ZhangZhenli
 */
public class MQConnectivityReceiver extends BroadcastReceiver {

	private static final String TAG = "MQConnectivityReceiver.java";
	private static final boolean DBG = true;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			Log.w(TAG, "onReceived() called with " + intent);
			return;
		}

		final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		Intent serviceIntent = new Intent(context, PushService.class);
		if (networkInfo != null) {
			if (networkInfo.isConnected()) {
				serviceIntent.setAction(MqttIntent.CONNECT);
				context.startService(serviceIntent);
				log("Connectivity changed: isConnected");
			} else if (networkInfo.isConnectedOrConnecting()) {
				log("Connectivity changed: isConnectedOrConnecting");
			} else {
			}
		} else {
			log("Connectivity changed: networkInfo is Null");
			serviceIntent.setAction(MqttIntent.DISCONNECT);
			IBinder iBinder = peekService(context, serviceIntent);
			if (iBinder != null) {
				log("Connectivity changed: iBinder != null");
				context.startService(serviceIntent);
			} else {
				log("Connectivity changed: iBinder == null");
			}

			LocalBinder result = (LocalBinder) peekService(context, serviceIntent);
			if (result == null) {
				log("Connectivity changed: iBinder == null1");
				context.startService(serviceIntent);
				result = (LocalBinder) peekService(context, serviceIntent);
			}
			notifyContextService(context);
		}
	}

	private void notifyContextService(final Context context) {
		log("notifyContextService");
		final IBinder iBinder = peekService(context, new Intent(context, TestService.class));
		// communicate the event only if the context service is already running
		if (iBinder != null) {
			log("notifyContextService changed: iBinder != null1");
		}
	}

	/**
	 * Utility method to handle logging. If 'quietMode' is set, this method does nothing
	 * 
	 * @param message the message to log
	 */
	private void log(String message) {
		if (DBG) {
			Log.i(TAG, message);
		}
	}
}
