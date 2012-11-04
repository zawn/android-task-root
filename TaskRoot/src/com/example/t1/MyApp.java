package com.example.t1;

import android.util.Log;
import cn.mimail.sdk.app.MiApp;

public class MyApp extends MiApp {
	private static final String TAG = "MyApp";
	public static Profile profile;
	public String profiles = "zhangzhangli123";

	@Override
	public void onCreate() {
		Log.i(TAG, "MyApp.onCreate()-------------------------------------------------------------");
		super.onCreate();
	}

	/*
	 * @see android.app.Application#onLowMemory()
	 */
	@Override
	public void onLowMemory() {
		Log.i(TAG, "MyApp.onLowMemory()-------------------------------------------------------------");
		super.onLowMemory();
	}

	/**
	 * @param profile 要设置的 profile
	 */
	public static void setProfile(Profile profile) {
		MyApp.profile = profile;
	}

}
