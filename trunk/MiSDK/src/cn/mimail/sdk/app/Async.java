/*
 * Name   Async.java
 * Author ZhangZhenli
 * Created on 2012-10-29, 下午7:09:08
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package cn.mimail.sdk.app;

import java.lang.ref.WeakReference;
import java.util.Arrays;

import android.util.Log;
import cn.mimail.misdk.BuildConfig;

/**
 * 通过回调函数实现后台任务与UI任务的分离
 * 
 * @author ZhangZhenli
 */
public abstract class Async<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

	private static final String TAG = "Async.java";
	private WeakReference<Callback> mCallback = null;

	static {
		AsyncTask.setDefaultExecutor(THREAD_POOL_EXECUTOR);
	}

	/**
	 * 构造函数
	 */
	public Async() {
		super();
	}

	/**
	 * Bind a {@link Callback} object to this Async.
	 * 
	 * @param cb The client's Callback implementation.
	 * 
	 * @see #getCallback()
	 */
	public final void setCallback(Callback cb) {
		mCallback = new WeakReference<Callback>(cb);
	}

	/**
	 * Return the current {@link Callback} implementation Callback to this Async.
	 * 
	 * @return A {@link Callback} instance or null if no callback was set.
	 * 
	 * @see #setCallback
	 */
	public Callback getCallback() {
		if (mCallback != null) {
			return mCallback.get();
		}
		return null;
	}

	/**
	 * 回调函数
	 */
	public static interface Callback {
		/**
		 * <p>
		 * Runs on the UI thread after {@link #doInBackground}. The specified result is the value returned by {@link #doInBackground}.
		 * </p>
		 * 
		 * <p>
		 * This method won't be invoked if the task was cancelled.
		 * </p>
		 * 
		 * @param result The result of the operation computed by {@link #doInBackground}.
		 * 
		 * @see #onPreExecute
		 * @see #doInBackground
		 * @see #onCancelled(Object)
		 */
		public <Result> void onPostExecute(Result result);

		/**
		 * <p>
		 * Runs on the UI thread after {@link #cancel(boolean)} is invoked and {@link #doInBackground(Object[])} has finished.
		 * </p>
		 * 
		 * <p>
		 * The default implementation simply invokes {@link #onCancelled()} and ignores the result. If you write your own implementation, do not call
		 * <code>super.onCancelled(result)</code>.
		 * </p>
		 * 
		 * @param result The result, if any, computed in {@link #doInBackground(Object[])}, can be null
		 * 
		 * @see #cancel(boolean)
		 * @see #isCancelled()
		 */
		public <Result> void onCancelled(Result result);
	}

	/**
	 * 回调函数
	 */
	public static interface Callback2 extends Callback {

		/**
		 * Runs on the UI thread before {@link #doInBackground}.
		 * 
		 * @see #onPostExecute
		 * @see #doInBackground
		 */
		public void onPreExecute();

		/**
		 * Runs on the UI thread after {@link #publishProgress} is invoked. The specified values are the values passed to {@link #publishProgress}.
		 * 
		 * @param values The values indicating progress.
		 * 
		 * @see #publishProgress
		 * @see #doInBackground
		 */
		public <Progress> void onProgressUpdate(Progress... values);
	}

	@Override
	protected void onPreExecute() {
		if (BuildConfig.DEBUG)
			Log.i(TAG, "onPreExecute");
		final Callback callback = getCallback();
		if (callback != null && (callback instanceof Callback2)) {
			((Callback2) callback).onPreExecute();
		}
	}

	@Override
	protected void onPostExecute(Result result) {
		if (BuildConfig.DEBUG)
			Log.i(TAG, "onPostExecute");
		final Callback callback = getCallback();
		if (callback != null) {
			callback.onPostExecute(result);
		}
	}

	@Override
	protected void onProgressUpdate(Progress... values) {
		if (BuildConfig.DEBUG)
			Log.i(TAG, "onProgressUpdate" + Arrays.toString(values));
		final Callback callback = getCallback();
		if (callback != null && (callback instanceof Callback2)) {
			((Callback2) callback).onProgressUpdate(values);
		}
	}

	@Override
	protected void onCancelled(Result result) {
		if (BuildConfig.DEBUG)
			Log.i(TAG, "onCancelled");
		final Callback callback = getCallback();
		if (callback != null) {
			callback.onCancelled(result);
		}
	}

}
