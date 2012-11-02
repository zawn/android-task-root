/*
 * Name   JsonWorker.java
 * Author ZhangZhenli
 * Created on 2012-10-30, 上午11:42:59
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package cn.mimail.sdk.net.json;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Hashtable;

import android.util.Log;
import cn.mimail.misdk.BuildConfig;
import cn.mimail.sdk.app.Async;
import cn.mimail.sdk.app.Async.Callback;
import cn.mimail.sdk.app.Async.Callback2;
import cn.mimail.sdk.json.JsonReaderable;

/**
 * 异步工作线程,定义Json获取过程中的异步模式
 * 
 * @author ZhangZhenli
 */
public abstract class JsonWorker {

	private static final String TAG = "JsonWorker.java";

	private boolean mExitTasksEarly = false;
	protected boolean mPauseWork = false;
	private final Object mPauseWorkLock = new Object();

	public void setPauseWork(boolean pauseWork) {
		synchronized (mPauseWorkLock) {
			mPauseWork = pauseWork;
			if (!mPauseWork) {
				mPauseWorkLock.notifyAll();
			}
		}
	}

	public void setExitTasksEarly(boolean exitTasksEarly) {
		mExitTasksEarly = exitTasksEarly;
	}

	private static Hashtable<String, HttpWorkerTask<? extends JsonReaderable>> mAlreadyTask = new Hashtable<String, HttpWorkerTask<? extends JsonReaderable>>();

	public <Result extends JsonReaderable> void load(String data, Result result, Async.Callback cb) {
		if (data == null) {
			return;
		}

		if (cancelPotentialWork(data, result)) {
			final HttpWorkerTask<Result> task = new HttpWorkerTask<Result>(result);
			HttpWorkerTaskCallback httpWorkerTaskCallback = new HttpWorkerTaskCallback();
			httpWorkerTaskCallback.setCallback(cb);
			task.setCallback(httpWorkerTaskCallback);
			mAlreadyTask.put(data, task);
			// NOTE: This uses a custom version of AsyncTask that has been
			// pulled from the
			// framework and slightly modified. Refer to the docs at the top of
			// the class
			// for more info on what was changed.
			task.executeOnExecutor(Async.THREAD_POOL_EXECUTOR, data);
		}
	}

	/**
	 * 检查相同目的的工作线程是否存在
	 * 
	 * @param data
	 * @param result
	 * @return true:如果不存在这样的工作线程,或者该工作线程已经被取消或者,false:如果该工作线程正在处理过程中
	 */
	public static <Result extends JsonReaderable> boolean cancelPotentialWork(Object data, Result result) {
		final HttpWorkerTask<? extends JsonReaderable> httpWorkerTask = getHttpWorkerTask(data);

		if (httpWorkerTask != null) {
			final Object workData = httpWorkerTask.data;
			if (workData == null || !workData.equals(data)) {
				httpWorkerTask.cancel(true);
				mAlreadyTask.remove(data);
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "cancelPotentialWork - cancelled work for " + data);
				}
			} else {
				// The same work is already in progress.
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "cancelPotentialWork - The same work is already in progress." + data);
				}
				return false;
			}
		}
		return true;
	}

	/**
	 * 返回与给定对象相关联的HttpWorkerTask
	 * 
	 * @param result
	 * @return 相关联的HttpWorkerTask,如果这样的HttpWorkerTask不存在,返回null
	 */
	private static <Result extends JsonReaderable> HttpWorkerTask<? extends JsonReaderable> getHttpWorkerTask(Object data) {
		if (data != null) {
			if (mAlreadyTask.containsKey(data)) {
				return (HttpWorkerTask<? extends JsonReaderable>) mAlreadyTask.get(data);
			}
		}
		return null;
	}

	/**
	 * 在异步线程中完成实际的下载操作
	 * 
	 * @param <Result>
	 * @author ZhangZhenli
	 */
	protected class HttpWorkerTask<Result extends JsonReaderable> extends Async<String, Integer, Result> {

		private Object data;
		private final WeakReference<Result> resultReference;

		public Result getResult() {
			if (resultReference != null) {
				return resultReference.get();
			}
			return null;
		}

		private HttpWorkerTask(Result result) {
			super();
			resultReference = new WeakReference<Result>(result);
		}

		/**
		 * Background processing.
		 */
		@Override
		protected Result doInBackground(String... params) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "doInBackground - starting work");
			}

			data = params[0];
			final String dataString = String.valueOf(data);

			// Wait here if work is paused and the task is not cancelled
			synchronized (mPauseWorkLock) {
				while (mPauseWork && !isCancelled()) {
					try {
						mPauseWorkLock.wait();
					} catch (InterruptedException e) {
					}
				}
			}

			Result result = null;
			if (!mExitTasksEarly) {
				result = processHttp(params[0], this);
			}

			mAlreadyTask.remove(data);

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "doInBackground - finished work");
			}

			return result;
		}

	}

	/**
	 * 处理处理实际的Http操作,该方法在异步线程中执行
	 * 
	 * @param string
	 * @param resultReference2
	 * @return
	 */
	protected abstract <Result extends JsonReaderable> Result processHttp(String urlString, HttpWorkerTask<Result> httpWorkerTask);

	/**
	 * 下载操作在本地线程中回调函数
	 * 
	 * @author ZhangZhenli
	 */
	private class HttpWorkerTaskCallback implements Callback2 {

		private WeakReference<Callback> mCallback = null;

		@Override
		public <Result> void onPostExecute(Result result) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "onPostExecute");
			}
			// if cancel was called on this task or the "exit early" flag is set
			// then we're done
			if (!mExitTasksEarly) {
				final Callback callback = getCallback();
				if (callback != null) {
					callback.onPostExecute(result);
				}
			}
		}

		@Override
		public <Result> void onCancelled(Result result) {
			synchronized (mPauseWorkLock) {
				mPauseWorkLock.notifyAll();
			}
			if (BuildConfig.DEBUG)
				Log.i(TAG, "onCancelled");
			final Callback callback = getCallback();
			if (callback != null) {
				callback.onCancelled(result);
			}
		}

		@Override
		public void onPreExecute() {
			if (BuildConfig.DEBUG)
				Log.i(TAG, "onPreExecute");
			final Callback callback = getCallback();
			if (callback != null && (callback instanceof Callback2)) {
				((Callback2) callback).onPreExecute();
			}
		}

		@Override
		public <Progress> void onProgressUpdate(Progress... values) {
			if (BuildConfig.DEBUG)
				Log.i(TAG, "onProgressUpdate" + Arrays.toString(values));
			final Callback callback = getCallback();
			if (callback != null && (callback instanceof Callback2)) {
				((Callback2) callback).onProgressUpdate(values);
			}
		}

		/**
		 * Bind a {@link Callback} object to this Async.
		 * 
		 * @param cb
		 *            The client's Callback implementation.
		 * 
		 * @see #getCallback()
		 */
		public final void setCallback(Callback cb) {
			mCallback = new WeakReference<Callback>(cb);
		}

		/**
		 * Return the current {@link Callback} implementation Callback to this
		 * Async.
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

	}
}
