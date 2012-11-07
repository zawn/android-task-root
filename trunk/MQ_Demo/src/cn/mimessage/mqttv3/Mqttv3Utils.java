/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.mimessage.mqttv3;

import android.os.Build;

/**
 * Class containing some static utility methods.
 */
public class Mqttv3Utils {
	public static final int IO_BUFFER_SIZE = 8 * 1024;

	private Mqttv3Utils() {
	};

	/**
	 * Workaround for bug pre-Froyo, see here for more info: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
	 */
	public static void disableConnectionReuseIfNecessary() {
		// HTTP connection reuse which was buggy pre-froyo
		if (hasHttpConnectionBug()) {
			System.setProperty("http.keepAlive", "false");
		}
	}

	/**
	 * Check if OS version has a http URLConnection bug. See here for more information:
	 * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
	 * 
	 * @return
	 */
	public static boolean hasHttpConnectionBug() {
		return Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO;
	}

	public static String getBrokerUrl() {
		String broker = "192.168.1.2";
		int port = 1883;
		return "tcp://" + broker + ":" + port;
	}

	public static String getClientId() {
		return "13912345678";
	}

	public static boolean getQuietMode() {
		return false;
	}
}
