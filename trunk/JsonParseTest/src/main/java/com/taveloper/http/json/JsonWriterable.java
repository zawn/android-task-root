/*
 * Name   JsonWriterable.java
 * Author ZhangZhenli
 * Created on 2012-10-31, 下午4:17:16
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package com.taveloper.http.json;

import java.io.Writer;

/**
 * 类通过实现 {@link JsonReaderable } 接口以启用其Json序列化功能。
 * 
 * @author ZhangZhenli
 */
public interface JsonWriterable {

	/**
	 * 将对象序列化到输出流
	 * 
	 * @param out 输出流
	 */
	public void writerJson(Writer out);
}
