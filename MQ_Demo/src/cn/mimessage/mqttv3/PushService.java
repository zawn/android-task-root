/*
 * Name   PushService.java
 * Author ZhangZhenli
 * Created on 2012-9-27, 下午6:11:02
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package cn.mimessage.mqttv3;

import java.sql.Timestamp;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDefaultFilePersistence;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
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
	public IBinder onBind(Intent intent) {
		log("onBind");
		return new LocalBinder();
	}

	public class LocalBinder extends Binder {
		public PushService getService() {
			return PushService.this;
		}
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(TAG, "onHandleIntent Start");
		Log.i(TAG, Thread.currentThread().getId() + "  " + Long.toString(System.currentTimeMillis()));
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
				reConnect();
			} else if (MqttIntent.DISCONNECT.equals(a)) {
				log("Terminate the connection");
				stopSelf();
			}
		} catch (MqttException e) {
			e.printStackTrace();
		}
		Log.i(TAG, Thread.currentThread().getId() + "  " + Long.toString(System.currentTimeMillis()));
		Log.i(TAG, "onHandleIntent End");
	}

	private void connect() {

		this.brokerUrl = Mqttv3Utils.getBrokerUrl();
		this.quietMode = Mqttv3Utils.getQuietMode();

		// This stores files in a cache directory
		String tmpDir = Utils.getDiskCacheDir(getApplication(), "13900000000").getPath();

		try {
			MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);
			// Construct the object that contains connection parameters
			// such as cleansession and LWAT
			conOpt = new MqttConnectOptions();
			conOpt.setCleanSession(false);

			// Construct the MqttClient instance
			client = new MqttClient(this.brokerUrl, Mqttv3Utils.getClientId(), dataStore);

			// Set this wrapper as the callback handler
			client.setCallback(this);
			// Connect to the server
			client.connect(conOpt);
			log("Connected to " + brokerUrl + " with client ID " + client.getClientId());

		} catch (MqttException e) {
			e.printStackTrace();
			log("Unable to set up client: " + e.toString());
		}
	}

	/**
	 * 
	 */
	private void reConnect() {
		// TODO 自动生成的方法存根
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

	private void reConnectIfNecessary() {
		log("reConnectIfNecessary");
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
