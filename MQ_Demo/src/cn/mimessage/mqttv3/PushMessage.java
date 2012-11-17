/*
 * Name   PushMessage.java
 * Author ZhangZhenli
 * Created on 2012-10-8, 下午8:19:22
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package cn.mimessage.mqttv3;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * 
 * @author ZhangZhenli
 */
public class PushMessage extends MqttMessage implements Serializable {

	private static final long serialVersionUID = 708456467437832908L;
	private String topicName;

	/**
	 * @param topic
	 */
	public PushMessage(String topicName) {
		this.topicName = topicName;
	}

	/**
	 * @param topic
	 * @param content
	 */
	public PushMessage(String topic, String content) {
		this(topic);
		try {
			this.setPayload(content.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public PushMessage(String topicName, MqttMessage message) throws MqttException {
		this(topicName, message.getPayload(), message.getQos());
	}

	/**
	 * @param name
	 * @param string
	 * @param qos2
	 */
	public PushMessage(String topic, String content, int qos) {
		this(topic, content);
		this.setQos(qos);
	}

	/**
	 * @param topicName2
	 * @param payload
	 * @param qos
	 */
	public PushMessage(String topicName, byte[] payload, int qos) {
		this(topicName, payload);
		this.setQos(qos);
	}

	/**
	 * @param topicName2
	 * @param payload
	 */
	public PushMessage(String topicName, byte[] payload) {
		this(topicName);
		this.setPayload(payload);
	}

	/**
	 * @return topicName
	 */
	public String getTopicName() {
		return topicName;
	}

	/**
	 * @param topicName 要设置的 topicName
	 */
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	@Override
	public byte[] getPayload() {
		try {
			return super.getPayload();
		} catch (MqttException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getPayloadString() {
		try {
			return new String(getPayload(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(getPayload());
		result = prime * result + getQos();
		result = prime * result + (isRetained() ? 1231 : 1237);
		result = prime * result + ((topicName == null) ? 0 : topicName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PushMessage other = (PushMessage) obj;
		if (!Arrays.equals(getPayload(), other.getPayload()))
			return false;
		if (getQos() != other.getQos())
			return false;
		if (isRetained() != other.isRetained())
			return false;
		if (topicName == null) {
			if (other.topicName != null)
				return false;
		} else if (!topicName.equals(other.topicName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PushMessage [topicName=" + topicName + ", getPayload()=" + Arrays.toString(getPayload())
				+ ", isRetained()=" + isRetained() + ", getQos()=" + getQos() + "]";
	}

}
