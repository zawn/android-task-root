/*
 * Name   MQMessage.java
 * Author ZhangZhenli
 * Created on 2012-10-8, 下午8:19:22
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package cn.mimessage.mqttv3;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 
 * @author ZhangZhenli
 */
public class MQMessage implements Serializable {

	private static final long serialVersionUID = 708456467437832908L;
	private String topicName;
	private int qos;
	private byte[] payload;
	/**
	 * @param topic
	 */
	public MQMessage(String topicName) {
		this.topicName = topicName;
	}
	/**
	 * @param topic
	 * @param content
	 */
	public MQMessage(String topic, String content) {
		this(topic);
		this.payload = content.getBytes();
	}
	/**
	 * @param name
	 * @param string
	 * @param qos2
	 */
	public MQMessage(String topic, String content, int qos) {
		this(topic, content);
		this.qos = qos;
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
	/**
	 * @return qos
	 */
	public int getQos() {
		return qos;
	}
	/**
	 * @param qos 要设置的 qos
	 */
	public void setQos(int qos) {
		this.qos = qos;
	}
	/**
	 * @return payload
	 */
	public byte[] getPayload() {
		return payload;
	}
	/**
	 * @param payload 要设置的 payload
	 */
	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
	/**
	 * @param payload 要设置的 payload
	 */
	public void setPayload(String payload) {
		this.payload = payload.getBytes();
	}
	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(payload);
		result = prime * result + qos;
		result = prime * result + ((topicName == null) ? 0 : topicName.hashCode());
		return result;
	}
	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MQMessage other = (MQMessage) obj;
		if (!Arrays.equals(payload, other.payload))
			return false;
		if (qos != other.qos)
			return false;
		if (topicName == null) {
			if (other.topicName != null)
				return false;
		} else if (!topicName.equals(other.topicName))
			return false;
		return true;
	}
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MQMessage [\n topicName=" + topicName + ",\n qos=" + qos + ",\n payload=" + Arrays.toString(payload) + "]";
	}
	
	
}
