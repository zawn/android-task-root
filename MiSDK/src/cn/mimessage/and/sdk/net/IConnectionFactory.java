package cn.mimessage.and.sdk.net;

import java.util.Observable;

import cn.mimessage.and.sdk.util.log.LogAdapter;

public interface IConnectionFactory {
	public static final int NETWORK_IS_AVAILABLE = 1;
	public static final int NETWORK_MAY_BE_AVAILABLE = 2;
	public static final int NETWORK_NOT_AVAILABLE = 0;

	/**
	 * 获取一个HttpClient对象
	 * 
	 * @return
	 */
	public YiYouHttpClient getClient();

	/**
	 * 判断网络是否正常
	 * 
	 * @return
	 */
	public boolean getNetworkWorked();

	/**
	 * 判断本session中的网络是否工作正常
	 * 
	 * @return
	 */
	public boolean getNetworkWorkedThisSession();

	/**
	 * @return
	 */
	public int isNetworkAvailable();

	/**
	 * 通知失败
	 */
	public void notifyFailure();

	/**
	 * 登记网络正常
	 * 
	 * @param isSuccess
	 * @return
	 */
	public boolean registerNetworkSuccess(boolean isSuccess);

	/**
	 * 重置网络设置
	 */
	public void resetNetworkSettings();

	/**
	 * 设置log适配器
	 * 
	 * @param logAdapter
	 */
	public void setLogAdapter(LogAdapter logAdapter);

	/**
	 * 设置网络配置观察者
	 * 
	 * @param ob
	 */
	public void setSettingObservable(Observable ob);
}
