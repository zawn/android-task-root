package cn.mimessage.and.sdk.net;

import java.io.IOException;

import org.apache.http.HttpHost;

public interface IHttpConnectionFactory extends IConnectionFactory {
	/**
	 * 创建一个链接
	 * 
	 * @param url
	 * @param isHttpPost
	 * @return
	 * @throws IOException
	 * @throws SecurityException
	 */
	public IYiYouHttpConnection createConnection(String url, boolean isHttpPost) throws IOException, SecurityException;

	/**
	 * 设置代理
	 * 
	 * @param proxyHost
	 */
	public void setProxy(HttpHost proxyHost);

	/**
	 * 设置超时时间
	 * 
	 * @param duration
	 */
	public void setTimeOut(int duration);
}
