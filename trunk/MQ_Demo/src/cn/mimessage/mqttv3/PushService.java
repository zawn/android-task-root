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

import cn.mimail.sdk.util.Utils;

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
public abstract class PushService extends LoopService {

	private static final String TAG = "PushService";
	private PushClient mClient;
	private final Timer timer = new Timer("Push Service Connect Change");
	private RealHandlerConnectChange connectTimeTask;
	private final Object timeTaskLock = new Object();
	private PushCallback mCallback;

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
	final protected void handleNewIntent(Intent intent) {
		Log.i(TAG, "handleNewIntent Start------");
		Log.i(TAG, "Thread Id:" + Thread.currentThread().getId() + "Thread Name:" + Thread.currentThread().getName());
		String a = intent.getAction();
		PushMessage message = (PushMessage) intent.getSerializableExtra(PushIntent.MESSAGE);
		// 在收到断开连接的消息终止服务,在不需要启用后台服务的时候忽略网络变化以及连接丢失
		if (PushIntent.DISCONNECT.equals(a)
				|| (!isEnableBackgroundService() && !Utils.isApplicationForeground(getApplicationContext()) && (PushIntent.CONNECT_CHANGE
						.equals(a) || PushIntent.CONNECT_LOST.equals(a)))) {
			log("Terminate the connection");
			Log.w(TAG, "Stop Service");
			stopSelf();
			return;
		}
		if (mClient == null) {
			 mCallback = getPushCallback();
			final PushConfig config = getPushConfig();
			if (mCallback == null || config == null) {
				Log.e(TAG, "The PushCallback or PushConfig object is null");
				throw new NullPointerException("The PushCallback or PushConfig object is null");
			}
			mClient = PushClient.getInstance(getApplicationContext(), mCallback, config);
		}
		// Do an appropriate action based on the intent.
		try {
			if (PushIntent.CONNECT.equals(a)) {
				log("To establish a connection with the server");
				mClient.connect();
			} else if (PushIntent.PUBLISH.equals(a)) {
				log("Publish message:" + message.toString());
				mClient.publish(message);
			} else if (PushIntent.SUBSCRIBE.equals(a)) {
				log("Subscribing to topic:" + message.toString());
				mClient.subscribe(message.getTopicName(), message.getQos());
			} else if (PushIntent.UNSUBSCRIBE.equals(a)) {
				log("Unsubscribe topic:" + message.toString());
				mClient.unsubscribe(message.getTopicName());
			} else if (PushIntent.KEEPALIVE.equals(a)) {
				log("KeepAlive...");
				mClient.keepAlive();
			} else if (PushIntent.RECONNECT.equals(a)) {
				log("Reconnect...");
				mClient.reconnect();
			} else if (PushIntent.CONNECT_CHANGE.equals(a)) {
				log("connect_change");
				handlerConnectChange();
			} else if (PushIntent.CONNECT_LOST.equals(a)) {
				log("CONNECT_LOST");
				handlerConnectChange();
			} else if (PushIntent.NOTIFICATION_READ.equals(a)) {
				log("NOTIFICATION_READ");
				mCallback.notificationRead();
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

	final public class RealHandlerConnectChange extends TimerTask {

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

	/**
	 * 返回消息的回调函数
	 * 
	 * @return
	 */
	public abstract PushCallback getPushCallback();

	/**
	 * 返回连接的配置信息
	 * 
	 * @return
	 */
	public abstract PushConfig getPushConfig();

	/**
	 * 是否启用后台服务,默认启用后台服务.如果无需启用/或者需要灵活控制是否启用后台服务,子类需要覆盖此方法. 注意:在不启用后台服务的时候在软件关闭前程序需要主动关闭服务.
	 * 
	 * @return true 启用后台服务,false 不启用后台服务
	 */
	public boolean isEnableBackgroundService() {
		return true;
	}
}
