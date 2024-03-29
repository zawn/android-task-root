/*
 * Name   LoopService.java
 * Author ZhangZhenli
 * Created on 2012-9-27, 下午6:11:02
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package cn.mimessage.mqttv3;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

/**
 * 该类与{@link IntentService}功能基本一致,唯一的区别在于该类不会自动终止.
 * 
 * @see android.app.IntentService
 * 
 * @author ZhangZhenli
 */
public abstract class LoopService extends Service {
	private volatile Looper mServiceLooper;
	private volatile ServiceHandler mServiceHandler;
	private String mName;
	private boolean mRedelivery;

	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			handleNewIntent((Intent) msg.obj);
			// 处理完消息后不自动退出
			// stopSelf(msg.arg1);
		}
	}

	/**
	 * Creates an IntentService. Invoked by your subclass's constructor.
	 * 
	 * @param name Used to name the worker thread, important only for debugging.
	 */
	public LoopService(String name) {
		super();
		mName = name;
	}

	/**
	 * Sets intent redelivery preferences. Usually called from the constructor with your preferred semantics.
	 * 
	 * <p>
	 * If enabled is true, {@link #onStartCommand(Intent, int, int)} will return {@link Service#START_REDELIVER_INTENT}, so if this process dies
	 * before {@link #handleNewIntent(Intent)} returns, the process will be restarted and the intent redelivered. If multiple Intents have been sent,
	 * only the most recent one is guaranteed to be redelivered.
	 * 
	 * <p>
	 * If enabled is false (the default), {@link #onStartCommand(Intent, int, int)} will return {@link Service#START_NOT_STICKY}, and if the process
	 * dies, the Intent dies along with it.
	 */
	public void setIntentRedelivery(boolean enabled) {
		mRedelivery = enabled;
	}

	@Override
	public void onCreate() {
		// TODO: It would be nice to have an option to hold a partial wakelock
		// during processing, and to have a static startService(Context, Intent)
		// method that would launch the service & hand off a wakelock.

		super.onCreate();
		HandlerThread thread = new HandlerThread("LoopService[" + mName + "]");
		thread.start();

		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;
		msg.obj = intent;
		mServiceHandler.sendMessage(msg);
	}

	/**
	 * You should not override this method for your IntentService. Instead, override {@link #onHandleIntent}, which the system calls when the
	 * IntentService receives a start request.
	 * 
	 * @see android.app.Service#onStartCommand
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		onStart(intent, startId);
		return mRedelivery ? START_REDELIVER_INTENT : START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		mServiceLooper.quit();
	}

	/**
	 * Unless you provide binding for your service, you don't need to implement this method, because the default implementation returns null.
	 * 
	 * @see android.app.Service#onBind
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * This method is invoked on the worker thread with a request to process. Only one Intent is processed at a time, but the processing happens on a
	 * worker thread that runs independently from other application logic. So, if this code takes a long time, it will hold up other requests to the
	 * same IntentService, but it will not hold up anything else. When all requests have been handled, the IntentService stops itself, so you should
	 * not call {@link #stopSelf}.
	 * 
	 * @param intent The value passed to {@link android.content.Context#startService(Intent)}.
	 */
	protected abstract void handleNewIntent(Intent intent);
}
