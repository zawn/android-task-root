/*
 * Name   TestService.java
 * Author ZhangZhenli
 * Created on 2012-11-7, 下午8:04:51
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package cn.mimessage.mqttv3;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * 
 * @author ZhangZhenli
 */
public class TestService extends Service {

	private static final String TAG = "TestService.java";

	/*
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "onHandleIntent Start");
		Binder binder = new Binder();
		return binder;
	}
}
