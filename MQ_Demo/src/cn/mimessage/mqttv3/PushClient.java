/*
 * Name   PushClient.java
 * Author ZhangZhenli
 * Created on 2012-11-13, 上午11:26:38
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package cn.mimessage.mqttv3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Properties;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDefaultFilePersistence;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import android.content.Context;
import android.util.Log;

import cn.mimail.sdk.util.Utils;

/**
 * 
 * @author ZhangZhenli
 */
public class PushClient extends MqttClient {

	private static String mServerURI;
	private static String mClientId;
	private static MqttClientPersistence mPersistence;
	private static PushClient mPushClient;
	private static MqttConnectOptions mConnectOptions;
	private static PushCallback mCallback;
	private Context mConext;

	/**
	 * @param serverURI
	 * @param clientId
	 * @param persistence
	 * @throws MqttException
	 */
	public PushClient(String serverURI, String clientId, MqttClientPersistence persistence) throws MqttException {
		super(serverURI, clientId, persistence);
	}

	private static final String TAG = "PushClient.java";

	public static PushClient getInstance(Context context) {
		if (mPushClient == null) {
			initMqttClient(context);
		}
		return mPushClient;
	}

	private static MqttClient initMqttClient(Context context) {
		File logFile = new File(Utils.getDiskFilesDir(context), "mqtt-trace.properties");
		if (!logFile.exists()) {
			Properties traceProperties = new Properties();
			try {
				traceProperties.load(context.getAssets().open("mqtt-trace.properties"));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			String directory = traceProperties.getProperty("org.eclipse.paho.client.mqttv3.trace.outputName");
			traceProperties.setProperty("org.eclipse.paho.client.mqttv3.trace.outputName",
					Utils.getDiskFilesDir(context, directory).getAbsolutePath());
			try {
				traceProperties.store(new FileOutputStream(logFile), null);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		System.setProperty("org.eclipse.paho.client.mqttv3.trace", logFile.getAbsolutePath());

		mServerURI = Mqttv3Utils.getBrokerUrl();

		// This stores files in a cache directory
		String tmpDir = Utils.getDiskCacheDir(context, "13900000000").getPath();

		try {
			mPersistence = new MqttDefaultFilePersistence(tmpDir);
		} catch (MqttPersistenceException e) {
			e.printStackTrace();
		}
		// Construct the object that contains connection parameters
		// such as cleansession and LWAT
		mConnectOptions = new MqttConnectOptions();
		mConnectOptions.setCleanSession(false);

		mCallback = new PushCallback(context);
		mClientId = Mqttv3Utils.getClientId();

		try {
			// Construct the MqttClient instance
			mPushClient = new PushClient(mServerURI, mClientId, mPersistence);
			// Set this wrapper as the callback handler
			mPushClient.setCallback(mCallback);
			return mPushClient;
		} catch (MqttException e) {
			e.printStackTrace();
			Log.e(TAG, "Unable to set up client: " + e.toString());
		}
		return null;
	}

	/**
	 * 连接服务器该方法使用
	 * 
	 * @throws MqttSecurityException
	 * @throws MqttException
	 */
	@Override
	public void connect() throws MqttSecurityException, MqttException {
		Log.i(TAG, "Connected to " + mServerURI + " with client ID " + mClientId);
		this.connect(mConnectOptions);
	}

	public void reConnectIfNecessary() {
		Log.i(TAG, "reConnectIfNecessary");
	}

	/**
	 * 检查连接是否有效,无效则重新连接,否则维持现有连接
	 * 
	 * @param type
	 */
	public void reconnectIfNecessary(int type) {
		Log.i(TAG, "reConnectIfNecessary");
		// TODO 优化重练逻辑
		reconnect();
	}

	/**
	 * 重新连接服务器
	 */
	public void reconnect() {
		Log.i(TAG, "reconnect start");
		try {
			if (this.isConnected()) {
				if (BuildConfig.DEBUG)
					Log.i(TAG, "reconnect disconnect start");
				this.disconnect(1000);
				if (BuildConfig.DEBUG)
					Log.i(TAG, "reconnect disconnect end");
			}
			Log.i(TAG, "Successfully disconnected");
		} catch (MqttException e) {
			e.printStackTrace();
		} finally {
			try {
				this.connect(mConnectOptions);
				if (BuildConfig.DEBUG) {
					Log.i(TAG, "Connected to " + this.getServerURI() + " with client ID " + this.getClientId());
					Log.i(TAG, "Successfully connected to the server");
				}
			} catch (MqttSecurityException e) {
				e.printStackTrace();
			} catch (MqttException e) {
				e.printStackTrace();
			}
		}
		Log.i(TAG, "reconnect end");
	}

	/**
	 * Performs a single publish<br />
	 * 发布单条消息,注意该消息将不被保留
	 * 
	 * @param topicName
	 * @param qos
	 * @param payload
	 * @throws MqttException
	 */
	public void publish(PushMessage message) throws MqttException {
		publish(message.getTopicName(), message);
	}

	/**
	 * Performs a single publish<br />
	 * 发布单条消息,注意该消息将不被保留
	 * 
	 * @param topicName
	 * @param qos
	 * @param payload
	 * @throws MqttException
	 */
	public void publish(String topicName, MqttMessage message) throws MqttException {
		// Get an instance of the topic
		final MqttTopic topic = this.getTopic(topicName);
		// Publish the message
		String time = new Timestamp(System.currentTimeMillis()).toString();
		Log.i(TAG, "Publishing at: " + time + " to topic \"" + topicName + "\" qos " + message.getQos());
		MqttDeliveryToken token = topic.publish(message);
		// Wait until the message has been delivered to the server
		token.waitForCompletion();
	}

	/**
	 * Performs a single publish<br />
	 * 发布单条消息,注意该消息将不被保留
	 * 
	 * @param topicName
	 * @param qos
	 * @param payload
	 * @throws MqttException
	 */
	public void publish(String topicName, int qos, byte[] payload) throws MqttException {
		publish(topicName, qos, payload, false);
	}

	/**
	 * Performs a single publish<br />
	 * 发布单条消息
	 * 
	 * @param topicName the topic to publish to
	 * @param qos the qos to publish at
	 * @param payload the payload of the message to publish
	 * @param retained
	 * @throws MqttException
	 */
	public void publish(String topicName, int qos, byte[] payload, boolean retained) throws MqttException {
		// Get an instance of the topic
		final MqttTopic topic = this.getTopic(topicName);
		// Publish the message
		String time = new Timestamp(System.currentTimeMillis()).toString();
		Log.i(TAG, "Publishing at: " + time + " to topic \"" + topicName + "\" qos " + qos);
		MqttDeliveryToken token = topic.publish(payload, qos, retained);
		// Wait until the message has been delivered to the server
		token.waitForCompletion();
	}

	/**
	 * 
	 */
	public void keepAlive() {
		Log.i(TAG, "keepAlive");
	}
}
