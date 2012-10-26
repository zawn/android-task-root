/*
 * Name   JsonEntity.java
 * Author ZhangZhenli
 * Created on 2012-10-17, 下午3:45:46
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package org.apache.http.entity.mime;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 * @author ZhangZhenli
 */
public class JsonEntity extends StringEntity {

	private static final String TAG = "JsonEntity.java";

	public JsonEntity(JSONArray s) throws UnsupportedEncodingException {
		super(s.toString(), HTTP.UTF_8);
		setContentType("application/json" + HTTP.CHARSET_PARAM + HTTP.UTF_8);
	}

	public JsonEntity(JSONObject s) throws UnsupportedEncodingException {
		super(s.toString(), HTTP.UTF_8);
		setContentType("application/json" + HTTP.CHARSET_PARAM + HTTP.UTF_8);
	}
}
