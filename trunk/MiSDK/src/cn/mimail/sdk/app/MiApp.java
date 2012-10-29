package cn.mimail.sdk.app;

import android.app.Application;
import android.content.Context;
import cn.mimail.sdk.profile.Profile;

public class MiApp extends Application {
	private static Context instance;
	private static Profile profile;

	private static Class<?> mDefaultActivityClass;

	/**
	 * 此方法由TaskRoot调用,用于TaskRoot引导默认的Activity
	 * 
	 * @return 程序需要启动的Activity
	 */
	protected static Class<?> getDefaultActivityClass() {
		if (mDefaultActivityClass == null) {
			throw new RuntimeException("Please set a Activity to launch");
		}
		return mDefaultActivityClass;
	}

	/**
	 * @param 程序需要启动的第一个Activity
	 */
	protected static void setDefaultActivityClass(Class<?> defaultActivityClass) {
		mDefaultActivityClass = defaultActivityClass;
	}

	public static Context getInstance() {
		if (instance == null) {
			throw new RuntimeException("Context instance is null!!");
		}
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
	}

	/**
	 * @return profile
	 * @throws Exception
	 */
	public static Profile getProfile() throws NullPointerException {
		if (profile == null) {
			throw new NullPointerException("Context profile is null!!");
		}
		return profile;
	}

	/**
	 * @param profile 要设置的 profile
	 */
	public void setProfile(Profile profile) {
		MiApp.profile = profile;
	}

}
