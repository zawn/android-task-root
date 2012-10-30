/*
 * Name   Log.java
 * Author ZhangZhenli
 * Created on 2012-10-29, 下午5:31:45
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package cn.mimail.sdk.util;

import android.annotation.TargetApi;


/**
 * 
 * @author ZhangZhenli
 */
public class Log {

	private static final String TAG = "Log.java";

	/**
	 * Priority constant for the println method; use Log.v.
	 */
	public static final int VERBOSE = 2;

	/**
	 * Priority constant for the println method; use Log.d.
	 */
	public static final int DEBUG = 3;

	/**
	 * Priority constant for the println method; use Log.i.
	 */
	public static final int INFO = 4;

	/**
	 * Priority constant for the println method; use Log.w.
	 */
	public static final int WARN = 5;

	/**
	 * Priority constant for the println method; use Log.e.
	 */
	public static final int ERROR = 6;

	/**
	 * Priority constant for the println method.
	 */
	public static final int ASSERT = 7;


	private Log() {
	}

	/**
	 * Send a {@link #VERBOSE} log message.
	 * 
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 */
	public static int v(String tag, String msg) {
		return android.util.Log.v(tag, msg);
	}

	/**
	 * Send a {@link #VERBOSE} log message and log the exception.
	 * 
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @param tr An exception to log
	 */
	public static int v(String tag, String msg, Throwable tr) {
		return android.util.Log.v(tag, msg, tr);
	}

	/**
	 * Send a {@link #DEBUG} log message.
	 * 
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 */
	public static int d(String tag, String msg) {
		return android.util.Log.d(tag, msg);
	}

	/**
	 * Send a {@link #DEBUG} log message and log the exception.
	 * 
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @param tr An exception to log
	 */
	public static int d(String tag, String msg, Throwable tr) {
		return android.util.Log.d(tag, msg, tr);
	}

	/**
	 * Send an {@link #INFO} log message.
	 * 
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 */
	public static int i(String tag, String msg) {
		return android.util.Log.i(tag, msg);
	}

	/**
	 * Send a {@link #INFO} log message and log the exception.
	 * 
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @param tr An exception to log
	 */
	public static int i(String tag, String msg, Throwable tr) {
		return android.util.Log.i(tag, msg, tr);
	}

	/**
	 * Send a {@link #WARN} log message.
	 * 
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 */
	public static int w(String tag, String msg) {
		return android.util.Log.w(tag, msg);
	}

	/**
	 * Send a {@link #WARN} log message and log the exception.
	 * 
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @param tr An exception to log
	 */
	@TargetApi(8)
	public static int w(String tag, String msg, Throwable tr) {
		return android.util.Log.wtf(tag, msg, tr);
	}

	/**
	 * Checks to see whether or not a log for the specified tag is loggable at the specified level.
	 * 
	 * The default level of any tag is set to INFO. This means that any level above and including INFO will be logged. Before you make any calls to a
	 * logging method you should check to see if your tag should be logged. You can change the default level by setting a system property: 'setprop
	 * log.tag.&lt;YOUR_LOG_TAG> &lt;LEVEL>' Where level is either VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT, or SUPPRESS. SUPPRESS will turn off all
	 * logging for your tag. You can also create a local.prop file that with the following in it: 'log.tag.&lt;YOUR_LOG_TAG>=&lt;LEVEL>' and place
	 * that in /data/local.prop.
	 * 
	 * @param tag The tag to check.
	 * @param level The level to check.
	 * @return Whether or not that this is allowed to be logged.
	 * @throws IllegalArgumentException is thrown if the tag.length() > 23.
	 */
	public static native boolean isLoggable(String tag, int level);

	/*
	 * Send a {@link #WARN} log message and log the exception.
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param tr An exception to log
	 */
	public static int w(String tag, Throwable tr) {
		return android.util.Log.w(tag, tr);
	}

	/**
	 * Send an {@link #ERROR} log message.
	 * 
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 */
	public static int e(String tag, String msg) {
		return android.util.Log.e(tag, msg);
	}

	/**
	 * Send a {@link #ERROR} log message and log the exception.
	 * 
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @param tr An exception to log
	 */
	public static int e(String tag, String msg, Throwable tr) {
		return android.util.Log.e(tag, msg, tr);
	}

	/**
	 * What a Terrible Failure: Report a condition that should never happen. The error will always be logged at level ASSERT with the call stack.
	 * Depending on system configuration, a report may be added to the {@link android.os.DropBoxManager} and/or the process may be terminated
	 * immediately with an error dialog.
	 * 
	 * @param tag Used to identify the source of a log message.
	 * @param msg The message you would like logged.
	 */
	public static int wtf(String tag, String msg) {
		return wtf(tag, msg, null);
	}

	/**
	 * What a Terrible Failure: Report an exception that should never happen. Similar to {@link #wtf(String, String)}, with an exception to log.
	 * 
	 * @param tag Used to identify the source of a log message.
	 * @param tr An exception to log.
	 */
	public static int wtf(String tag, Throwable tr) {
		return wtf(tag, tr.getMessage(), tr);
	}

	/**
	 * What a Terrible Failure: Report an exception that should never happen. Similar to {@link #wtf(String, Throwable)}, with a message as well.
	 * 
	 * @param tag Used to identify the source of a log message.
	 * @param msg The message you would like logged.
	 * @param tr An exception to log. May be null.
	 */
	@TargetApi(8)
	public static int wtf(String tag, String msg, Throwable tr) {
		return android.util.Log.wtf(tag, msg, tr);
	}

	/**
	 * Handy function to get a loggable stack trace from a Throwable
	 * 
	 * @param tr An exception to log
	 */
	public static String getStackTraceString(Throwable tr) {
		return android.util.Log.getStackTraceString(tr);
	}

	/**
	 * Low-level logging call.
	 * 
	 * @param priority The priority/type of this log message
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @return The number of bytes written.
	 */
	public static int println(int priority, String tag, String msg) {
		return android.util.Log.println(priority, tag, msg);
	}
}
