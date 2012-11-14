/*
 * Name   PushService.java
 * Author ZhangZhenli
 * Created on 2012-9-27, 下午6:11:02
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package cn.mimessage.mqttv3;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.paho.client.mqttv3.MqttException;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * 继承{@link LoopService}并实现具体业务逻辑的子类
 * 
 * @see android.app.IntentService
 * 
 * @author ZhangZhenli
 */
public class PushService extends LoopService {

	private static final String TAG = "PushService";
	private PushClient mClient;
	private final Timer timer = new Timer();
	private RealHandlerConnectChange connectTimeTask;
	private final Object timeTaskLock = new Object();

	public PushService() {
		super("PushService");
	}

	@Override
	public void onCreate() {
		log("onCreate");
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		log("onDestroy");
		super.onDestroy();
		try {
			if (mClient != null) {
				if (mClient.isConnected()) {
					mClient.disconnect();
				}
				mClient = null;
			}
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void handleNewIntent(Intent intent) {
		Log.i(TAG, "handleNewIntent Start------");
		Log.i(TAG, "Thread Id:" + Thread.currentThread().getId() + "Thread Name:" + Thread.currentThread().getName());
		String a = intent.getAction();
		PushMessage message = (PushMessage) intent.getSerializableExtra(MqttIntent.MSG);
		if (mClient == null) {
			mClient = PushClient.getInstance(getApplicationContext());
		}
		// Do an appropriate action based on the intent.
		try {
			if (MqttIntent.CONNECT.equals(a)) {
				log("To establish a connection with the server");
				mClient.connect();
			} else if (MqttIntent.PUBLISH.equals(a)) {
				log("Publish message:" + message.toString());
				mClient.publish(message);
			} else if (MqttIntent.SUBSCRIBE.equals(a)) {
				log("Subscribing to topic:" + message.toString());
				mClient.subscribe(message.getTopicName(), message.getQos());
			} else if (MqttIntent.UNSUBSCRIBE.equals(a)) {
				log("Unsubscribe topic:" + message.toString());
				mClient.unsubscribe(message.getTopicName());
			} else if (MqttIntent.KEEPALIVE.equals(a)) {
				log("KeepAlive...");
				mClient.keepAlive();
			} else if (MqttIntent.RECONNECT.equals(a)) {
				log("Reconnect...");
				mClient.reconnect();
			} else if (MqttIntent.DISCONNECT.equals(a)) {
				log("Terminate the connection");
				stopSelf();
			} else if (MqttIntent.CONNECT_CHANGE.equals(a)) {
				log("connect_change");
				handlerConnectChange();
			} else {
				if (BuildConfig.DEBUG) {
					Log.w(TAG, "onHandleIntent intent action Undefined");
				}
			}
		} catch (MqttException e) {
			e.printStackTrace();
		}
		Log.i(TAG, "handleNewIntent End------");
	}

	/**
	 * 处理网络环境变化
	 */
	private void handlerConnectChange() {
		Log.e(TAG, "handlerConnectChange");
		synchronized (timeTaskLock) {
			if (connectTimeTask != null) {
				Log.w(TAG, "connectTimeTask != null");
				connectTimeTask.cancel();
			}
			connectTimeTask = new RealHandlerConnectChange();
			// 将任务延迟10秒,等待网络稳定后执行
			timer.schedule(connectTimeTask, 10000);
		}
	}

	public class RealHandlerConnectChange extends TimerTask {

		@Override
		public void run() {
			synchronized (timeTaskLock) {
				if (BuildConfig.DEBUG)
					Log.i("RealHandlerConnectChange", "Thread Id:" + Thread.currentThread().getId() + ", Thread Name:"
							+ Thread.currentThread().getName());
				final ConnectivityManager cm = (ConnectivityManager) getApplication().getSystemService(
						Context.CONNECTIVITY_SERVICE);
				final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
				if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
					Log.w(TAG, "checkConnection - no connection found");
					// 没有可用网络,关闭服务
					stopSelf();
					return;
				}
				final int type = networkInfo.getType();
				if (BuildConfig.DEBUG) {
					// err on side of caution
					log("networkInfo.getType()" + networkInfo.getType() + "   " + networkInfo.getTypeName());
				}
				mClient.reconnectIfNecessary(type);
			}
		}
	}

	/**
	 * Utility method to handle logging. If 'DEBUG' is set, this method does nothing
	 * 
	 * @param message the message to log
	 */
	private void log(String message) {
		if (!BuildConfig.DEBUG) {
			Log.i(TAG, message);
		}
	}

}
