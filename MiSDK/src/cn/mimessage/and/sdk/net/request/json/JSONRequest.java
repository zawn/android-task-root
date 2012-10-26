package cn.mimessage.and.sdk.net.request.json;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.json.JSONArray;

import cn.mimessage.and.sdk.net.IYiYouHttpConnection;
import cn.mimessage.and.sdk.net.bridge.IHttpListener;
import cn.mimessage.and.sdk.net.request.Request;
import cn.mimessage.and.sdk.profile.Config;
import cn.mimessage.and.sdk.util.log.LogX;

public abstract class JSONRequest extends Request {

	private IHttpListener mListener;
	private JSONArray mJsonParam;

	/**
	 * @param listener
	 * @param isPost
	 */
	public JSONRequest(Config config, IHttpListener listener) {
		super(config);
		mListener = listener;
	}

	public void setListener(IHttpListener listener) {
		mListener = listener;
	}

	public abstract String getPrefix();

	public abstract String getAction();

	public Header[] getHeaders() {
		return null;
	}

	public abstract List<NameValuePair> getPostParams();

	@Override
	protected void HttpConnect(boolean isPost) {
		final String prefix = getPrefix();
		final String action = getAction();
		int responseCode = -1;

		long start = System.currentTimeMillis();

		IYiYouHttpConnection conn = null;
		HttpEntity entity = null;

		try {
			final StringBuffer uri = new StringBuffer();
			uri.append(prefix);
			uri.append(action);

			final List<NameValuePair> nvps = getPostParams();
			mJsonParam = getJsonParam();

			if (!isPost && nvps != null) {
				uri.append(getParamStr(nvps));
			}

			LogX.d(this, "*****(" + (isPost ? "Post" : "Get") + ") URL : " + uri.toString());
			conn = mHttpFactory.createConnection(uri.toString(), isPost);
			if (conn == null) {
				throw new IOException("conn is null;");
			}

			conn.setHeads(getHeaders());

			if (isPost) {
				if (nvps != null) {
					conn.setEntity(nvps);
				} else if (mJsonParam != null) {
					conn.setEntity(mJsonParam);
				} else {
					throw new RuntimeException("Request params may not be null in post method.");
				}
			}
			responseCode = conn.getResponseCode();
			LogX.e(this, "*****Response Code : " + responseCode);

			conn.handleResponseStatusCode(responseCode);
			entity = conn.getResponseEntity();

			notifySuccess(entity);
		} catch (SocketTimeoutException e) {
			notifyFailure(responseCode, "SocketTimeoutException");
			e.printStackTrace();
		} catch (IOException e) {
			notifyFailure(responseCode, e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			notifyFailure(responseCode, "Exception");
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (IOException e) {
				notifyFailure(responseCode, "close IOException");
				e.printStackTrace();
			}
		}

		LogX.d(this, "*****Time Slide : " + (System.currentTimeMillis() - start));
	}
	
	/**
	 * @return mJsonParame
	 */
	public JSONArray getJsonParam() {
		return mJsonParam;
	}

	/**
	 * @param mJsonParam 要设置的 mJsonParame
	 */
	public void setJsonParam(JSONArray mJsonParam) {
		this.mJsonParam = mJsonParam;
	}

	protected void notifySuccess(HttpEntity entity) {
		if (mListener != null) {
			mListener.onHttpSuccess(entity, mBindObj);
		}
	}

	protected void notifyFailure(int code, String why) {
		if (mListener != null) {
			mListener.onHttpFailure(code, why, mBindObj);
		}
	}

	private String getParamStr(final List<NameValuePair> nvps) {
		final StringBuffer params = new StringBuffer();
		params.append('?');

		NameValuePair nvp = null;
		Iterator<NameValuePair> it = nvps.iterator();
		while (it.hasNext()) {
			if (params.length() > 1) {
				params.append('&');
			}
			nvp = it.next();
			params.append(nvp.getName());
			params.append('=');
			params.append(nvp.getValue());
		}
		return params.toString();
	}
}
