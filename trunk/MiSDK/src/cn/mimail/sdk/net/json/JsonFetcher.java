/*
 * Name   JsonFetcher.java
 * Author ZhangZhenli
 * Created on 2012-10-30, 上午11:43:31
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package cn.mimail.sdk.net.json;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Build;
import android.util.Log;
import cn.mimail.sdk.json.JsonReaderable;

/**
 * 
 * @author ZhangZhenli
 */
public class JsonFetcher extends JsonWorker {

	private static final String TAG = "JsonFetcher.java";

	private static int connect_timeout = 5000;
	private static final int IO_BUFFER_SIZE = 8 * 1024;

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
		JsonFetcher.connect_timeout = connect_timeout;
	}

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

	@Override
	protected <Result extends JsonReaderable> Result processHttp(String urlString, HttpWorkerTask<Result> task) {
		Result result = task.getResult();
		if (result != null) {
			return downloadUrlToObject(urlString, result);
		}
		return null;
	}

	/**
	 * Download a bitmap from a URL and write the content to an output stream.
	 * 
	 * @param urlString
	 *            The URL to fetch
	 * @return true if successful, false otherwise
	 */
	public <Result extends JsonReaderable> Result downloadUrlToObject(String urlString, Result result) {
		disableConnectionReuseIfNecessary();
		HttpURLConnection urlConnection = null;
		InputStreamReader in = null;

		try {
			final URL url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setConnectTimeout(connect_timeout);
			urlConnection.setDoOutput(false);
			urlConnection.setUseCaches(false);
			in = new InputStreamReader(urlConnection.getInputStream(), "UTF-8");
			result.readJson(in);
			Log.e(TAG, "downloadUrlToObject urlString:" + urlString + "to Object Success");
			return result;
		} catch (final IOException e) {
			Log.e(TAG, "Error in urlString:" + urlString + "to Object error - " + e);
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			try {
				if (in != null) {
					in.close();
				}
			} catch (final IOException e) {
			}
		}
		return null;
	}
}
