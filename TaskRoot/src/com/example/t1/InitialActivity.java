/*
 * Name   InitialActivity.java
 * Author Zawn
 * Created on 2012-10-22, 上午11:57:58
 *
 * Copyright (c) 2012. All rights reserved
 *
 */
package com.example.t1;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;

/**
 * 该Activity没有视图界面,只用于接收Intent并启动相应的Activity,并作为程序的Task<br>
 * 的Root Activity存在,用于程序退出.同时,该类可以去除部分机型的启动白屏/黑屏,优化<br>
 * 启动效果. ActivityTask应是其所在的Task的根Activity,即在程序的<code>AndroidManifest.xml</code><br>
 * 中应声明为如下形式:
 * 
 * <pre>
 * {@code
 *  <activity
 * 	 android:name="com.tadevelop.sdk.app.InitialActivity"
 * 	 android:theme="@android:style/Theme.NoDisplay" >
 * 	 <intent-filter>
 * 		 <action android:name="android.intent.action.MAIN" />
 * 
 * 		 <category android:name="android.intent.category.LAUNCHER" />
 * 	 </intent-filter>
 *  </activity>
 *  <activity
 * 	 android:name=".MainActivity"
 * 	 android:permission="YOUR_PERMISSION" >
 * 	 <intent-filter>
 * 		 <action android:name="YOUR_PACKNAME.intent.action.MAIN" />
 * 
 * 		 <category android:name="android.intent.category.DEFAULT" />
 * 	 </intent-filter>
 *  </activity>
 * }
 * </pre>
 * <p>
 * 其中.MainActivity为程序需要启动的第一个Activity. 而YOUR_PERMISSION是为该Activity定义的权限,如果你不需要为该<br>
 * Activity定义权限则可省略.<br>
 * 或者,你也可以在AndroidManifest.xml中使用如下方式进行声明:
 * 
 * <pre>
 * {@code
 *  <activity
 * 	 android:name="com.tadevelop.sdk.app.InitialActivity"
 * 	 android:theme="@android:style/Theme.NoDisplay" >
 * 	 <intent-filter>
 * 		 <action android:name="android.intent.action.MAIN" />
 * 
 * 		 <category android:name="android.intent.category.LAUNCHER" />
 * 	 </intent-filter>
 * 
 * 	 <meta-data
 * 		 android:name="YOUR_PACKNAME.intent.action.MAIN"
 * 		 android:value=".MainActivity" />
 *  </activity>
 * }
 * </pre>
 * <p>
 * 其中.MainActivity为程序需要启动的第一个Activity.<br>
 * 如果在AndroidManifest.xml中同时存在上述两种声明方式,则会忽略第一种声明.
 * 
 * @author Zawn
 */
final public class InitialActivity extends Activity {

	private static final String TAG = "InitialActivity.java";
	private static final boolean DEBUG = true;

	public static final String DEFAULT_LAUNCH_ACTIVITY 	= ".intent.action.MAIN";			// intent 动作名, 默认的需要启动的Activity
	
	public static final String CURRENT_LAUNCH_ACTIVITY 	= "com.tadevelop.intent.data.CURRENT_LAUNCH";	// intent 数据名, 需要启动的目标Activity
	public static final String BUNDLE_DATA 				= "com.tadevelop.intent.data.BUNDLE_DATA";		// intent 数据名, 启动Activity是需附带的参数

	private static final String DEFAULT_LAUNCH_INTENT 	= "com.tadevelop.outState.DEFAULT_LAUNCH_INTENT";		// intent 数据名, 默认的需要启动的Activity
	private static final String ORIGINAL_INTENT 		= "com.tadevelop.outState.ORIGINAL_INTENT";				// intent 数据名, 该实例接收到的前一个Intent对象
	private static String mDefaultLaunchActivity;// intent 数据名, 默认的需要启动的Activity
	private static boolean mIsNewIntent;	// 标识该intent是否是新的Intent
	private boolean mInitiativeDestroy;		// 标识是否需要主动销毁自己
	private static Intent mDefaultLaunchInent;			// 需要启动的默认的Activity

	private void onHandleIntent(final Intent intent) {
		if (DEBUG)
			Log.i(TAG, "InitialActivity.onHandleIntent() TaskId:" + this.getTaskId());
		final Class<?> cls = (Class<?>) intent.getSerializableExtra(CURRENT_LAUNCH_ACTIVITY);
		final Bundle bundle = (Bundle) intent.getBundleExtra(BUNDLE_DATA);
		if (DEBUG)
			Log.i(TAG, "InitialActivity.Target class is : " + ((cls == null) ? "null" : cls.getName()));
		final Intent i;
		if (cls == null) {
			if (DEBUG)
				Log.i(TAG, "InitialActivity.Start the default activity : " + mDefaultLaunchInent.getComponent());
			i = mDefaultLaunchInent.cloneFilter();
			if (bundle != null) {
				i.putExtras(bundle);
			}
			startActivity(i);
		} else {
			if (cls.isInstance(InitialActivity.this)) {
				if ((Intent.FLAG_ACTIVITY_CLEAR_TOP & intent.getFlags()) == 0) {
					throw new RuntimeException(
							"InitialActivity is the root of this task.  If you want to exit this task, add Intent.FLAG_ACTIVITY_CLEAR_TOP flag in intent.");
				}
				initiativeDestroy();
			} else {
				i = new Intent(this, cls);
				if (bundle != null) {
					i.putExtras(bundle);
				}
				startActivity(i);
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (DEBUG)
			Log.i(TAG, "InitialActivity.onCreate() TaskId:" + this.getTaskId());
		super.onCreate(savedInstanceState);
		if (!isTaskRoot()) {
			throw new RuntimeException(
					"InitialActivity is not the root of this task. Please confirm the code correctly.");
		}

		if (savedInstanceState != null) {
			// 在低内存的情况下Activity将被销毁,销毁后下面两种操作将触发此条件
			// 1,用户按返回键重新返回当前Activity,此时程序应该自动退出.
			// 2,程序通过Intent启动该Activity.
			mIsNewIntent = false;
			setIntent((Intent) savedInstanceState.getParcelable(ORIGINAL_INTENT));
			mDefaultLaunchInent = (Intent) savedInstanceState.getParcelable(DEFAULT_LAUNCH_INTENT);
		} else {
			// 新的TaskRoot实例,是第一次接收到该Intent,所以置位为true
			mIsNewIntent = true;
			boolean fromLauncher = verifyStartFromLauncher();
			if (!fromLauncher) {
				return;
			}
			mDefaultLaunchInent = this.getDefaultLaunchIntent();
			// 这是一个新的TaskRoot实例,执行默认的操作
			final Intent intent = getIntent();
			onHandleIntent(intent);
		}
	}
	
	private boolean verifyStartFromLauncher() {
		Log.i(TAG, "InitialActivity.verifyStartFromLauncher()");
		Intent intent = getIntent();
		Intent other = getLauncherIntent();
		boolean filterEquals = intent.filterEquals(other);
		if (filterEquals) {
			return true;
		}else {
			finish();
			startActivity(other);
			return false;
		}
		
	}

	/**
	 * 重新启动程序
	 */
	private Intent getLauncherIntent() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		intent.setClass(this, this.getClass());
		return intent;
	}

	@Override
	final protected void onDestroy() {
		super.onDestroy();
		if (DEBUG)
			Log.i(TAG, "InitialActivity.onDestroy()");
		if (mInitiativeDestroy) {
			mInitiativeDestroy = false;
			if (DEBUG)
				Log.i(TAG, "InitialActivity.java Terminate this instance");
			android.os.Process.killProcess(android.os.Process.myPid());
			// 接下来的所有逻辑将不会被执行,有可能包括上一个Activity的onStop和onDestroy方法
		}
	}

	@Override
	final protected void onNewIntent(final Intent intent) {
		if (DEBUG)
			Log.i(TAG, "InitialActivity.onNewIntent()");
		mIsNewIntent = true;
		setIntent(intent);
		onHandleIntent(intent);
	}

	@Override
	final protected void onResume() {
		super.onResume();
		if (DEBUG)
			Log.i(TAG, "InitialActivity.onResume()");
		if (mIsNewIntent) {
			// 置位,等待下一次NewIntent的到来.
			if (DEBUG)
				Log.i(TAG, "mIsNewIntent = true");
			mIsNewIntent = false;
		} else {
			// 说明该Intent不是新传入的,这时候的返回栈中已经没有其他的Activity了,即Task中只有该实例了,应终止程序.
			if (DEBUG)
				Log.i(TAG,
						"mIsNewIntent = false,Intent is not a new incoming, Back stack has no other Activity, should terminate the program at this time.");
			initiativeDestroy();
		}
	}

	@Override
	final protected void onSaveInstanceState(Bundle outState) {
		if (DEBUG)
			Log.i(TAG, "InitialActivity.onSaveInstanceState()");
		outState.putParcelable(ORIGINAL_INTENT, getIntent());
		outState.putParcelable(DEFAULT_LAUNCH_INTENT, mDefaultLaunchInent);
		super.onSaveInstanceState(outState);
	}

	/**
	 * 主动销毁
	 */
	private void initiativeDestroy() {
		if (DEBUG)
			Log.i(TAG, "InitialActivity.initiativeDestroy()");
		mInitiativeDestroy = true;
		finish();
	}

	/**
	 * 用于切换程序逻辑分支的便利方法,执行该方法后会销毁在Back Stack中除 <br>
	 * {@link InitialActivity} 以外的所有{@link Activity} ,并启动方法参数 <code>cls </code>指定的 {@link Activity}<br>
	 * <br>
	 * 注:如果cls指定的{@link Activity}是{@link InitialActivity}本身,因为{@link InitialActivity}是不可见的此<br>
	 * 时{@link InitialActivity}将自我销毁,程序退出.
	 * 
	 * @param packageContext A Context of the application package implementing this class.
	 * @param cls The component class that is to be used for the intent.
	 */
	public static void switchActivity(Activity activity, Class<?> cls) {
		if (DEBUG)
			Log.i(TAG, "InitialActivity.switchActivity(), cls = " + cls.getName());
		switchActivity(activity, cls, null);
	}

	/**
	 * 用于切换程序逻辑分支的便利方法,执行该方法后会销毁在Back Stack中除 <br>
	 * {@link InitialActivity} 以外的所有{@link Activity} ,并启动方法参数 <code>cls </code>指定的 {@link Activity}<br>
	 * <br>
	 * 注:如果cls指定的{@link Activity}是{@link InitialActivity}本身,因为{@link InitialActivity}是不可见的此<br>
	 * 时{@link InitialActivity}将自我销毁,程序退出.
	 * 
	 * @param packageContext A Context of the application package implementing this class.
	 * @param cls The component class that is to be used for the intent,If NUll default components will be started
	 * @param bundle To attach to the intent of the parameters
	 */
	public static void switchActivity(Activity activity, Class<?> cls, Bundle bundle) {
		if (DEBUG)
			Log.i(TAG, "InitialActivity.switchActivity(), cls = " + cls.getName() + ", bundle = "
					+ (bundle == null ? "null" : bundle.toString()));
		Intent intent = new Intent(activity, InitialActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra(CURRENT_LAUNCH_ACTIVITY, cls);
		intent.putExtra(BUNDLE_DATA, bundle);
		activity.startActivity(intent);
	}

	/**
	 * 用于退出程序的便捷方法,该方法将销毁任务栈中的所有Activity并退出程序<br>
	 * 
	 * @param packageContext A Context of the application package implementing this class.
	 */
	public static void exitTask(Context context) {
		if (DEBUG)
			Log.i(TAG, "InitialActivity.exitTask()");
		Intent intent = new Intent(context, InitialActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra(CURRENT_LAUNCH_ACTIVITY, InitialActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 重新启动软件,注意,该重启方法并不能销毁Application对象,
	 * 
	 * @param packageContext A Context of the application package implementing this class.
	 */
	public static void reStart(Activity activity) {
		if (DEBUG)
			Log.i(TAG, "InitialActivity.reStart()");
		switchActivity(activity, null);
	}

	/**
	 * 返回默认的需要启动的Activity
	 */
	private Intent getDefaultLaunchIntent() {
		if (DEBUG)
			Log.i(TAG, "InitialActivity.getDefaultLaunchIntent()");
		mDefaultLaunchActivity = this.getPackageName() + DEFAULT_LAUNCH_ACTIVITY;
		String activityName = null;
		Intent intent = new Intent(getIntent());		
		try {
			PackageItemInfo activityInfo = getPackageManager().getActivityInfo(getComponentName(),
					PackageManager.GET_META_DATA);
			if (activityInfo.metaData != null) {
				activityName = activityInfo.metaData.getString(mDefaultLaunchActivity);
			}
		} catch (NameNotFoundException e) {
		}
		if (activityName == null) {
			Intent temp = new Intent(mDefaultLaunchActivity);
			List<ResolveInfo> resolveInfos = this.getPackageManager().queryIntentActivities(temp,
					PackageManager.GET_RESOLVED_FILTER);
			for (ResolveInfo resolveInfo : resolveInfos) {
				if (resolveInfo.activityInfo.packageName.equals(this.getPackageName())) {
					activityName = resolveInfo.activityInfo.name;
				}
			}
		}
		if (activityName != null && activityName.startsWith(".")) {
			activityName = getPackageName() + activityName;
		}
		if (activityName != null) {
			intent.setClassName(getApplicationContext(), activityName);
		} else {
			throw new RuntimeException(
					"InitialActivity did not find the default to launch Activity, make sure you provide the name of the class is correct and complete");
		}
		if (DEBUG) 
			Log.i(TAG, intent.toString());
		return intent;
	}
}
