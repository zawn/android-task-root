/*
 * Name   Profile.java
 * Author ZhangZhenli
 * Created on 2012-10-26, 上午11:42:17
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package cn.mimail.sdk.profile;

/**
 * 
 * @author ZhangZhenli
 */
public class Profile {

	private static final String TAG = "Profile.java";
	private static final Class<?> mDefaultActivityClass;

	/**
	 * 此方法由TaskRoot调用,用于TaskRoot引导默认的Activity
	 * 
	 * @return 程序需要启动的Activity
	 */
	public static Class<?> getDefaultActivityClass() {
		if (mDefaultActivityClass == null) {
			throw new RuntimeException("Please set a Activity to launch");
		}
		return mDefaultActivityClass;
	}

	/**
	 * @param 程序需要启动的第一个Activity
	 */
	public static void setDefaultActivityClass(Class<?> defaultActivityClass) {
		mDefaultActivityClass = defaultActivityClass;
	}

}
