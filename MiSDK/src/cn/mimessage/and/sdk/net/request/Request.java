package cn.mimessage.and.sdk.net.request;

import cn.mimessage.and.sdk.net.IHttpConnectionFactory;
import cn.mimessage.and.sdk.profile.Config;
import cn.mimessage.and.sdk.thread.ITunnelThread;
import cn.mimessage.and.sdk.thread.PoolRunnable;
import cn.mimessage.and.sdk.thread.TaskController;

public abstract class Request {
	protected IHttpConnectionFactory mHttpFactory;
	protected Object[] mBindObj;
	protected static final int DURATION = 20000;
	protected Config mConfig;

	public Request(Config config) {
		mConfig = config;
		mHttpFactory = config.getConnectionFactory();
		mHttpFactory.setTimeOut(getTimeOutDuration());
	}

	protected int getTimeOutDuration() {
		return DURATION;
	}

	public TaskController httpPost() {
		return mConfig.getThreadPool().addTask(new PoolRunnable() {
			@Override
			public void run() throws InterruptedException {
				HttpConnect(true);
			}
		});
	}

	public TaskController httpGet() {
		return mConfig.getThreadPool().addTask(new PoolRunnable() {
			@Override
			public void run() throws InterruptedException {
				HttpConnect(false);
			}
		});
	}

	public TaskController httpPost(ITunnelThread itt) {
		if (itt == null) {
			return null;
		}

		return itt.addTask(new PoolRunnable() {
			@Override
			public void run() throws InterruptedException {
				HttpConnect(true);
			}
		});
	}

	public TaskController httpGet(ITunnelThread itt) {
		if (itt == null) {
			return null;
		}

		return itt.addTask(new PoolRunnable() {
			@Override
			public void run() throws InterruptedException {
				HttpConnect(false);
			}
		});
	}

	protected abstract void HttpConnect(boolean isPost);

	public void bindData(Object... bindObj) {
		if (bindObj == null) {
			throw new IllegalArgumentException("If you wana bing something to your request, please don't bind null to her.");
		}

		mBindObj = bindObj;
	}
}
