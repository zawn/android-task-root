/*
 * Name   JsonReaderable.java
 * Author ZhangZhenli
 * Created on 2012-10-31, 下午3:50:00
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package cn.mimail.sdk.json;

import java.io.Reader;

/**
 * 类通过实现 {@link JsonReaderable } 接口以启用其Json反序列化功能。
 * 
 * @author ZhangZhenli
 */
public interface JsonReaderable {

	/**
	 * 从输入流 {@code Reader} 中读取Json格式的数据恢复对象
	 * 
	 * @param in Json输入流
	 */
	public void readJson(Reader in);

}
