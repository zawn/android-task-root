/*
 * Name   PermissionException.java
 * Author ZhangZhenli
 * Created on 2012-12-5, 下午2:05:03
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package cn.mimail.sdk.exception;

/**
 * 
 * @author ZhangZhenli
 */
public class PermissionException extends Exception {

	private static final long serialVersionUID = -2238149245515210412L;
	private static final String TAG = "PermissionException.java";

	/**
	 * 
	 */
	public PermissionException() {
		super();
		// TODO 自动生成的构造函数存根
	}

	/**
	 * @param detailMessage
	 * @param throwable
	 */
	public PermissionException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	/**
	 * @param detailMessage
	 */
	public PermissionException(String detailMessage) {
		super(detailMessage);
	}

	/**
	 * @param throwable
	 */
	public PermissionException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * 
	 */
	public PermissionException(SecurityException e) {
		this(e.getMessage(), e.getCause());
	}

}
