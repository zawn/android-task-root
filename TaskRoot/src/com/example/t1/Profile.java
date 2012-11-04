/*
 * Name   Profile.java
 * Author ZhangZhenli
 * Created on 2012-10-26, 上午11:42:17
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package com.example.t1;

/**
 * 
 * @author ZhangZhenli
 */
public class Profile {

	private static final String TAG = "Profile.java";
	private static String zhang;

	/**
	 * @return zhang
	 */
	public static String getZhang() {
		return zhang;
	}

	/**
	 * @param zhang 要设置的 zhang
	 */
	public static void setZhang(String zhang) {
		Profile.zhang = zhang;
	}
}
