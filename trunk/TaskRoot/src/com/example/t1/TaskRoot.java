/*
 * Name   RootActivity.java
 * Author Zawn
 * Created on 2012-10-22, 上午11:57:58
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package com.example.t1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

/**
 * TaskRoot应是其所在的Task的根Activity,即在程序的<code>AndroidManifest.xml</code><br>
 * 中声明为如下形式: <br>
 * <p>
 * &nbsp; &nbsp; &nbsp; &nbsp; &lt;activity<br />
 * &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; android:name="<i>YOUR_PACKAGE_NAME</i>.TaskRoot"<br />
 * &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; android:theme="@android:style/Theme.NoDisplay" &gt;<br />
 * &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &lt;intent-filter&gt;<br />
 * &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &lt;action android:name="android.intent.action.MAIN" /&gt;<br />
 * &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &lt;category android:name="android.intent.category.LAUNCHER" /&gt;<br />
 * &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &lt;/intent-filter&gt;<br />
 * &nbsp; &nbsp; &nbsp; &nbsp; &lt;/activity&gt;
 * </p>
 * <p>
 * <br />
 * </p>
 * 该Activity没有视图界面,只用于接收Intent并启动相应的Activity,并作为程序的Task<br>
 * 的Root存在,用于程序退出.
 * 
 * @author Zawn
 */
public final class TaskRoot extends Activity {

	private static final String TAG = "TaskRoot.java";
	private static boolean mIsNewIntent;
	private static final String ORIGINAL_INTENT = "original_intent";
	private static final String TARGET_CLASS = "cn.mimail.TARGET_CLASS";
	private boolean mInitiativeDestroy;

	/*
	 * 默认启动的Activity
	 */
	private static final Class<?> clazz = Activity1.class;

	private void intentHandler(final Intent intent) {
		final Class<?> cls = (Class<?>) intent.getSerializableExtra(TaskRoot.TARGET_CLASS);
		Log.i(TAG, "Target class is:" + ((cls == null) ? "null" : cls.getName()));
		final Intent i;
		if (cls == null) {
			Log.i(TAG, "Start the default activity");
			i = new Intent(this, clazz);
			startActivity(i);
		} else {
			if (cls.isInstance(TaskRoot.this)) {
				if ((Intent.FLAG_ACTIVITY_CLEAR_TOP & intent.getFlags()) == 0) {
					throw new RuntimeException(
							"TaskRoot is the root of this task.  If you want to exit this task, add Intent.FLAG_ACTIVITY_CLEAR_TOP flag in intent.");
				}
				initiativeDestroy();
			} else {
				i = new Intent(this, cls);
				startActivity(i);
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		if (!isTaskRoot()) {
			throw new RuntimeException("TaskRoot is not the root of this task.  The root is the first activity in a task.");
		}

		if (savedInstanceState != null) {
			// 在低内存的情况下Activity将被销毁,销毁后下面两种操作将触发此条件
			// 1,用户按返回键重新返回当前Activity,此时程序应该自动退出.
			// 2,程序通过Intent启动该Activity.
			mIsNewIntent = false;
			setIntent((Intent) savedInstanceState.getParcelable(ORIGINAL_INTENT));
		} else {
			// 新的TaskRoot实例,是第一次接收到该Intent,所以置位为true
			mIsNewIntent = true;
			// 这是一个新的TaskRoot实例,执行默认的操作
			final Intent intent = getIntent();
			intentHandler(intent);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy");
		if (mInitiativeDestroy) {
			mInitiativeDestroy = false;
			Log.i(TAG, "Start terminate instances");
			android.os.Process.killProcess(android.os.Process.myPid());
			// 接下来的所有逻辑将不会被执行,包括上一个Activity的onStop和onDestroy方法
		}
	}

	@Override
	protected void onNewIntent(final Intent intent) {
		Log.i(TAG, "onNewIntent");
		mIsNewIntent = true;
		setIntent(intent);
		intentHandler(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume");
		if (mIsNewIntent) {
			// 置位,等待下一次NewIntent的到来.
			Log.i(TAG, "mIsNewIntent = true");
			mIsNewIntent = false;
		} else {
			// 说明该Intent不是新传入的,既是通过返回键返回到该实例的,这是Task中只有该实例了,应终止程序.
			Log.i(TAG, "mIsNewIntent = false");
			initiativeDestroy();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.i(TAG, "onSaveInstanceState");
		outState.putParcelable(ORIGINAL_INTENT, getIntent());
		super.onSaveInstanceState(outState);
	}

	/**
	 * 主动销毁
	 */
	private void initiativeDestroy() {
		mInitiativeDestroy = true;
		finish();
	}

	/**
	 * 用于切换程序逻辑分支的便利方法,执行该方法后会销毁在Back Stack中除 <br>
	 * {@link TaskRoot} 以外的所有{@link Activity} ,并启动方法参数 <code>cls </code>指定的 {@link Activity}<br>
	 * <br>注:如果cls指定的{@link Activity}是{@link TaskRoot}本身,因为{@link TaskRoot}是不可见的此<br>时{@link TaskRoot}将自我销毁,程序退出.
	 * 
	 * @param packageContext A Context of the application package implementing this class.
	 * @param cls The component class that is to be used for the intent.
	 */
	public static void switchActivity(Context packageContext, Class<?> cls) {
		Intent intent = new Intent(packageContext, TaskRoot.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra(TARGET_CLASS, cls);
		packageContext.startActivity(intent);
	}
}
