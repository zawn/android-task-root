/*
 * Name   PushService.java
 * Author ZhangZhenli
 * Created on 2012-9-27, 下午6:11:02
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package cn.mimessage.mqttv3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDefaultFilePersistence;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.internal.trace.Trace;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.MediaStore.Files;
import android.util.Log;
import cn.mimail.sdk.util.FileUtils;
import cn.mimail.sdk.util.IOUtils;
import cn.mimail.sdk.util.Utils;

/**
 * 继承{@link LoopService}并实现具体业务逻辑的子类
 * 
 * @see android.app.IntentService
 * 
 * @author ZhangZhenli
 */
public class PushService extends LoopService implements MqttCallback {

	private static final String TAG = "PushService";
	private String brokerUrl; // the url to connect to
	private boolean quietMode; // whether debug should be printed to standard out
	private MqttConnectOptions conOpt;
	private MqttClient client;
	private final Timer timer = new Timer();
	private RealHandlerConnectChange connectTimeTask;
	private final Object timeTaskLock = new Object();

	/**
	 * @param name
	 */
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
			if (client != null) {
				client.disconnect();
				client = null;
			}
		} catch (MqttException e) {
			e.printStackTrace();
		}
		log("Disconnected");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(TAG, "onHandleIntent Start------");
		Log.i(TAG, "Thread Id:" + Thread.currentThread().getId() + "Thread Name:" + Thread.currentThread().getName());
		String a = intent.getAction();
		MQMessage message = (MQMessage) intent.getSerializableExtra(MqttIntent.MSG);
		// Do an appropriate action based on the intent.
		try {
			if (MqttIntent.CONNECT.equals(a)) {
				log("To establish a connection with the server");
				connect();
			} else if (MqttIntent.PUBLISH.equals(a)) {
				log("Publish message:" + message.toString());
				publish(message.getTopicName(), message.getQos(), message.getPayload());
			} else if (MqttIntent.SUBSCRIBE.equals(a)) {
				log("Subscribing to topic:" + message.toString());
				subscribe(message.getTopicName(), message.getQos());
			} else if (MqttIntent.UNSUBSCRIBE.equals(a)) {
				log("Unsubscribe topic:" + message.toString());
				unSubscribe(message.getTopicName());
			} else if (MqttIntent.KEEPALIVE.equals(a)) {
				log("KeepAlive...");
				keepAlive();
			} else if (MqttIntent.RECONNECT.equals(a)) {
				log("Reconnect...");
				reconnect();
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
		Log.i(TAG, "Thread Id:" + Thread.currentThread().getId() + "Thread Name:" + Thread.currentThread().getName());
		Log.i(TAG, "onHandleIntent End------");
	}

	/**
	 * 处理网络环境变化
	 */
	private void handlerConnectChange() {
		Log.e(TAG, "handlerConnectChange");
		synchronized (timeTaskLock) {
			if (connectTimeTask != null) {
				Log.e(TAG, "connectTimeTask != null");
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
				Log.i(TAG, "Thread Id:" + Thread.currentThread().getId() + ", Thread Name:"
						+ Thread.currentThread().getName());
				final ConnectivityManager cm = (ConnectivityManager) getApplication().getSystemService(
						Context.CONNECTIVITY_SERVICE);
				final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
				if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
					Log.e(TAG, "checkConnection - no connection found");
					// 没有可用网络,关闭连接
					stopSelf();
					return;
				}
				final int type = networkInfo.getType();
				// err on side of caution
				log("networkInfo.getType()" + networkInfo.getType() + "   " + networkInfo.getTypeName());

				if (networkInfo.isConnected()) {
					log("Connectivity changed: networkInfo is not Null and isConnected");
				}
				if (networkInfo.isConnectedOrConnecting()) {
					log("Connectivity changed: networkInfo is not Null and isConnectedOrConnecting");
				} else {
					log("Connectivity changed: networkInfo is not Null");
				}
				reconnectIfNecessary(type);
			}
		}
	}

	private MqttClient initMqttClient() {
		File logFile = new File(Utils.getDiskFilesDir(getApplicationContext()), "mqtt-trace.properties");
		if (!logFile.exists()) {
			Properties traceProperties = new Properties();
			try {
				traceProperties.load(getAssets().open("mqtt-trace.properties"));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			String directory = traceProperties.getProperty("org.eclipse.paho.client.mqttv3.trace.outputName");
			traceProperties.setProperty("org.eclipse.paho.client.mqttv3.trace.outputName",
					Utils.getDiskFilesDir(getApplicationContext(), directory).getAbsolutePath());
			try {
				traceProperties.store(new FileOutputStream(logFile), null);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		System.setProperty("org.eclipse.paho.client.mqttv3.trace", logFile.getAbsolutePath());

		this.brokerUrl = Mqttv3Utils.getBrokerUrl();
		this.quietMode = Mqttv3Utils.getQuietMode();

		// This stores files in a cache directory
		String tmpDir = Utils.getDiskCacheDir(getApplication(), "13900000000").getPath();

		MqttDefaultFilePersistence dataStore = null;
		try {
			dataStore = new MqttDefaultFilePersistence(tmpDir);
		} catch (MqttPersistenceException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		// Construct the object that contains connection parameters
		// such as cleansession and LWAT
		conOpt = new MqttConnectOptions();
		conOpt.setCleanSession(false);

		try {
			// Construct the MqttClient instance
			client = new MqttClient(this.brokerUrl, Mqttv3Utils.getClientId(), dataStore);
			// Set this wrapper as the callback handler
			client.setCallback(this);
			return client;
		} catch (MqttException e) {
			e.printStackTrace();
			log("Unable to set up client: " + e.toString());
		}
		return null;
	}

	private void initMqttClientIfNecessary() {
		if (client == null) {
			initMqttClient();
		}
	}

	private void connect() {

		initMqttClientIfNecessary();

		try {
			// Connect to the server
			client.connect(conOpt);
			log("Connected to " + brokerUrl + " with client ID " + client.getClientId());
		} catch (MqttSecurityException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	private void reConnectIfNecessary() {
		log("reConnectIfNecessary");
	}

	/**
	 * 检查连接是否有效,无效则重新连接,否则维持现有连接
	 * 
	 * @param type
	 */
	private void reconnectIfNecessary(int type) {
		log("reConnectIfNecessary");
		// TODO 优化重练逻辑
		reconnect();
	}

	/**
	 * 重新连接服务器
	 */
	private void reconnect() {
		initMqttClientIfNecessary();
		log("reconnect start");
		try {
			client.disconnect(1000);
			log("Successfully disconnected");
		} catch (MqttException e) {
			e.printStackTrace();
		} finally {
			try {
				client.connect(conOpt);
				log("Connected to " + brokerUrl + " with client ID " + client.getClientId());
				log("Successfully connected to the server");
			} catch (MqttSecurityException e) {
				e.printStackTrace();
			} catch (MqttException e) {
				e.printStackTrace();
			}
		}
		log("reconnect end");
	}

	/**
	 * 
	 */
	private void keepAlive() {
	}

	@Override
	public void connectionLost(Throwable cause) {
		log("connectionLost");
		try {
			client.connect(conOpt);
		} catch (MqttSecurityException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void messageArrived(MqttTopic topic, MqttMessage message) throws Exception {
		log("MessageArrived");
		// Called when a message arrives from the server.
		String time = new Timestamp(System.currentTimeMillis()).toString();
		log("Time:\t" + time + "  Topic:\t" + topic.getName() + "  Message:\t" + new String(message.getPayload())
				+ "  QoS:\t" + message.getQos());

		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		intent.setAction(MqttIntent.MSGARRIVED);
		intent.putExtra(MqttIntent.MSG,
				new MQMessage(topic.getName(), new String(message.getPayload()), message.getQos()));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
		log("MessageArrived End");
	}

	@Override
	public void deliveryComplete(MqttDeliveryToken token) {
		// Here use synchronous delivery, by using the token.waitForCompletion() call in the work thread.
	}

	/**
	 * Performs a single publish
	 * 
	 * @param topicName the topic to publish to
	 * @param qos the qos to publish at
	 * @param payload the payload of the message to publish
	 * @throws MqttException
	 */
	public void publish(String topicName, int qos, byte[] payload) throws MqttException {

		// Get an instance of the topic
		MqttTopic topic = client.getTopic(topicName);

		MqttMessage message = new MqttMessage(payload);
		message.setQos(qos);

		// Publish the message
		String time = new Timestamp(System.currentTimeMillis()).toString();
		log("Publishing at: " + time + " to topic \"" + topicName + "\" qos " + qos);
		MqttDeliveryToken token = topic.publish(message);

		// Wait until the message has been delivered to the server
		token.waitForCompletion();
	}

	/**
	 * Subscribes to a topic and blocks until Enter is pressed
	 * 
	 * @param topicName the topic to subscribe to
	 * @param qos the qos to subscibe at
	 * @throws MqttException
	 */
	public void subscribe(String topicName, int qos) throws MqttException {

		// Subscribe to the topic
		log("Subscribing to topic \"" + topicName + "\" qos " + qos);
		client.subscribe(topicName, qos);
	}

	/**
	 * @param topicName the topic to subscribe to
	 * @throws MqttException
	 * 
	 */
	public void unSubscribe(String topicName) throws MqttException {
		// Subscribe to the topic
		log("unSubscribe to topic \"" + topicName);
		client.unsubscribe(topicName);
	}

	/**
	 * Utility method to handle logging. If 'quietMode' is set, this method does nothing
	 * 
	 * @param message the message to log
	 */
	private void log(String message) {
		if (!quietMode) {
			Log.i(TAG, message);
		}
	}

}
