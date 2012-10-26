package cn.mimessage.and.sdk.net.bridge;

import org.apache.http.HttpEntity;

public interface IHttpListener {
	public void onHttpFailure(int errorCode, String why, Object... bindObj);

	public void onHttpSuccess(HttpEntity entity, Object... bindObj);
}
