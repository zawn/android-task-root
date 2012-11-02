/*
 * Name   HttpWorker.java
 * Author ZhangZhenli
 * Created on 2012-10-30, 上午11:44:39
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package cn.mimail.sdk.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Arrays;
import java.util.Hashtable;

import android.os.Build;
import android.util.Log;
import android.widget.ImageView;
import cn.mimail.misdk.BuildConfig;
import cn.mimail.sdk.app.Async;
import cn.mimail.sdk.app.Async.Callback;
import cn.mimail.sdk.app.Async.Callback2;

/**
 * 
 * @author ZhangZhenli
 */
public abstract class HttpWorker_back {

	private static final String TAG = "HttpWorker";
	private static final int IO_BUFFER_SIZE = 8 * 1024;
	private static int connect_timeout = 10000;

	private boolean mExitTasksEarly = false;
	protected boolean mPauseWork = false;
	private final Object mPauseWorkLock = new Object();

	@SuppressWarnings("rawtypes")
	private static Hashtable mAlreadyTask = new Hashtable();

	/**
	 * 连接的超时时间
	 * 
	 * @return connect_timeout
	 */
	public static int getConnectTimeout() {
		return connect_timeout;
	}

	/**
	 * 设置超时时间
	 * 
	 * @param connect_timeout
	 *            要设置的 connect_timeout
	 */
	public static void setConnectTimeout(int connect_timeout) {
		HttpWorker_back.connect_timeout = connect_timeout;
	}

	@SuppressWarnings("unchecked")
	protected <Result> void load(Object data, Result result) {
		if (data == null) {
			return;
		}

		if (cancelPotentialWork(data, result)) {
			mAlreadyTask.put(data, result);
			final HttpWorkerTask<Result> task = new HttpWorkerTask<Result>(result);
			// NOTE: This uses a custom version of AsyncTask that has been
			// pulled from the
			// framework and slightly modified. Refer to the docs at the top of
			// the class
			// for more info on what was changed.
			task.executeOnExecutor(Async.THREAD_POOL_EXECUTOR, String.valueOf(data));
		}
	}

	/**
	 * Returns true if the current work has been canceled or if there was no
	 * work in progress on this image view. Returns false if the work in
	 * progress deals with the same data. The work is not stopped in that case.
	 */
	public static <Result> boolean cancelPotentialWork(Object data, Result result) {
		final HttpWorkerTask<Result> httpWorkerTask = getHttpWorkerTask(result);

		if (httpWorkerTask != null) {
			final Object bitmapData = httpWorkerTask.data;
			if (bitmapData == null || !bitmapData.equals(data)) {
				httpWorkerTask.cancel(true);
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "cancelPotentialWork - cancelled work for " + data);
				}
			} else {
				// The same work is already in progress.
				return false;
			}
		}
		return true;
	}

	/**
	 * @param
	 * @return Retrieve the currently active work task (if any) associated with
	 *         this imageView. null if there is no such task.
	 */
	@SuppressWarnings("unchecked")
	private static <Result> HttpWorkerTask<Result> getHttpWorkerTask(Result result) {
		if (result != null) {
			if (mAlreadyTask.containsKey(result)) {
				return (HttpWorkerTask<Result>) mAlreadyTask.get(result);

			}
		}
		return null;
	}

	/**
	 * The actual AsyncTask that will asynchronously process the image.
	 */
	private class HttpWorkerTask<Result> extends Async<String, Integer, Result> {

		private Object data;
		private final WeakReference<Result> resultReference;

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
				result = processHttp(params[0]);
			}

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "doInBackground - finished work");
			}

			return result;
		}

		@Override
		protected void onCancelled(Result result) {
			synchronized (mPauseWorkLock) {
				mPauseWorkLock.notifyAll();
			}
			super.onCancelled(result);
		}

	}

	/**
	 * Download a bitmap from a URL and write the content to an output stream.
	 * 
	 * @param urlString
	 *            The URL to fetch
	 * @return true if successful, false otherwise
	 */
	public boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
		disableConnectionReuseIfNecessary();
		HttpURLConnection urlConnection = null;
		BufferedOutputStream out = null;
		BufferedInputStream in = null;

		try {
			final URL url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();
			in = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE);
			out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);

			int b;
			while ((b = in.read()) != -1) {
				out.write(b);
			}
			return true;
		} catch (final IOException e) {
			Log.e(TAG, "Error in downloadBitmap - " + e);
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (final IOException e) {
			}
		}
		return false;
	}

	/**
	 * @param string
	 */
	public abstract <Result> Result processHttp(String urlString);

	/**
	 * 向指定的urlString写入指定内容,并读取返回
	 * 
	 * @param urlString
	 * @return
	 */
	public boolean uploadStreamToUrl(String urlString) {
		disableConnectionReuseIfNecessary();
		HttpURLConnection urlConnection = null;
		BufferedOutputStream out = null;
		BufferedInputStream in = null;

		try {
			final URL url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setUseCaches(false);
			urlConnection.setDoOutput(true);
			urlConnection.setChunkedStreamingMode(0);
			urlConnection.setConnectTimeout(connect_timeout);
			urlConnection.connect();

			out = new BufferedOutputStream(urlConnection.getOutputStream(), IO_BUFFER_SIZE);
			writeStream(out);

			in = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE);
			readStream(in);

			return true;
		} catch (SocketTimeoutException e) {
			// TODO: handle exception
		} catch (final IOException e) {
			Log.e(TAG, "Error in downloadBitmap - " + e);
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (final IOException e) {
			}
		}
		return false;

	}

	/**
	 * 读取网路内容
	 * 
	 * @param in
	 */
	protected abstract void readStream(BufferedInputStream in);

	/**
	 * 向网络输出内容
	 * 
	 * @param out
	 */
	protected abstract void writeStream(BufferedOutputStream out);

	/**
	 * Workaround for bug pre-Froyo, see here for more info:
	 * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
	 */
	public static void disableConnectionReuseIfNecessary() {
		// HTTP connection reuse which was buggy pre-froyo
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
			System.setProperty("http.keepAlive", "false");
		}
	}

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
}
