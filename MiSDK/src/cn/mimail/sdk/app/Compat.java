/*
 * Name   Compat.java
 * Author ZhangZhenli
 * Created on 2012-8-15, 下午4:06:18
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package cn.mimail.sdk.app;

/**
 * 
 * @author ZhangZhenli
 */
public abstract class Compat {

	static final CompatImpl IMPL;
	static {
		final int version = android.os.Build.VERSION.SDK_INT;
		if (version >= 16 || android.os.Build.VERSION.CODENAME.equals("JellyBean")) {
			IMPL = new JBCompatImpl();
		} else if (version >= 14) {
			IMPL = new ICSCompatImpl();
		} else if (version >= 11) {
			IMPL = new HCCompatImpl();
		} else if (version >= 9) {
			IMPL = new GBCompatImpl();
		} else if (version >= 8) {
			IMPL = new FroyoCompatImpl();
		} else {
			IMPL = new BaseCompatImpl();
		}
	}

	interface CompatImpl {

	}

	static class BaseCompatImpl implements CompatImpl {

	}

	static class JBCompatImpl implements CompatImpl {

	}

	static class ICSCompatImpl implements CompatImpl {

	}

	static class HCCompatImpl implements CompatImpl {

	}

	static class GBCompatImpl implements CompatImpl {

	}

	static class FroyoCompatImpl implements CompatImpl {

	}
}
