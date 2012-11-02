/*
 * Name   HttpWorker.java
 * Author ZhangZhenli
 * Created on 2012-11-1, 下午5:41:16
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package cn.mimail.sdk.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import android.os.Build;
import android.util.Log;

/**
 * 
 * @author ZhangZhenli
 */
public abstract class HttpWorker {

	private static final String TAG = "HttpWorker.java";

	private static final int IO_BUFFER_SIZE = 8 * 1024;
	private static int connect_timeout = 10000;

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

	/**
	 * 向指定的urlString写入指定内容,并读取返回
	 * 
	 * @param urlString
	 * @return
	 */
	public boolean uploadStreamToUrl(String urlString, InputStream inputStream, OutputStream outputStream) {
		disableConnectionReuseIfNecessary();
		HttpURLConnection urlConnection = null;
		BufferedOutputStream outToHttp = null;
		BufferedInputStream inFromHttp = null;

		try {
			final URL url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setUseCaches(false);
			urlConnection.setDoOutput(true);
			urlConnection.setChunkedStreamingMode(0);
			urlConnection.setConnectTimeout(connect_timeout);
			urlConnection.connect();

			outToHttp = new BufferedOutputStream(urlConnection.getOutputStream(), IO_BUFFER_SIZE);
			
			writeStream(outToHttp);

			inFromHttp = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE);
			readStream(inFromHttp);

			return true;
		} catch (SocketTimeoutException e) {
			Log.e(TAG, "Error in downloadBitmap - " + e);
		} catch (final IOException e) {
			Log.e(TAG, "Error in downloadBitmap - " + e);
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			try {
				if (outToHttp != null) {
					outToHttp.close();
				}
				if (inFromHttp != null) {
					inFromHttp.close();
				}
			} catch (final IOException e) {
			}
		}
		return false;
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

}
