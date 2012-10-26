package cn.mimessage.and.sdk.net;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Locale;

import oig.apache.commons.codec.Charsets;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.JsonEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.NameFilePair;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import android.os.Build;
import cn.mimessage.and.sdk.profile.Config;
import cn.mimessage.and.sdk.util.FunctionUtils;
import cn.mimessage.and.sdk.util.log.LogX;

public class YiYouHttpConnectionFactory extends BaseConnectionFactory implements IHttpConnectionFactory {
	private static final String DEFAULT_USER_AGENT = "Mozilla/5.0(Linux; U; Android " + Build.VERSION.RELEASE + "; "
			+ Locale.getDefault().getLanguage() + "; " + Build.MODEL + ") AppleWebKit/533.0 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
	// private static final String DEFAULT_USER_AGENT =
	// "Mozilla/5.0 (iPhone; U; CPU iPhone OS 2_1 like Mac OS X;ja-jp) AppleWebKit/525.18.1 (KHTML, like Gecko) Version/3.1.1 Mobile/5F136 Safari/525.20";
	private static final int MAX_WORKER_THREAD_COUNT = 4;
	private static final Object lock = new Object();
	private static int numOpenConnection;
	private YiYouHttpClient mClient;
	private String userAgent;

	public YiYouHttpConnectionFactory(Config config) {
		super(config, "HttpWorks");
		userAgent = DEFAULT_USER_AGENT;
		LogX.d(this, "DEFAULT_USER_AGENT : " + DEFAULT_USER_AGENT);
		numOpenConnection = 0;
		ensureClient();
	}

	@Override
	public IYiYouHttpConnection createConnection(String url, boolean isHttpPost) throws IOException {
		ensureClient();
		return new DefaultHttpConnection(url, isHttpPost);
	}

	@Override
	public void setProxy(HttpHost proxyHost) {
		if (mClient != null) {
			mClient.enableProxy(proxyHost);
		}
	}

	@Override
	public void setTimeOut(int duration) {
		ensureClient();

		HttpParams params = mClient.getParams();
		// 设置链接超时时间20s(从发起连接到链接成功)
		HttpConnectionParams.setConnectionTimeout(params, duration);
		// 设置socket链接超时时间20s(等待数据获取的时间)
		HttpConnectionParams.setSoTimeout(params, duration);
	}

	@Override
	public YiYouHttpClient getClient() {
		return mClient;
	}

	@Override
	public int isNetworkAvailable() {
		return 2;
	}

	public void setUserAgent(String agent) {
		userAgent = agent;
	}

	private void ensureClient() {
		synchronized (lock) {
			if (mClient == null) {
				mClient = YiYouHttpClient.newInstance(userAgent);
				HttpParams localHttpParams = mClient.getParams();
				ConnPerRouteBean localConnPerRouteBean = new ConnPerRouteBean(MAX_WORKER_THREAD_COUNT);
				ConnManagerParams.setMaxConnectionsPerRoute(localHttpParams, localConnPerRouteBean);
			}
		}
	}

	class DefaultHttpConnection implements IYiYouHttpConnection {
		private ByteArrayOutputStream baos;
		private HttpEntity mHttpEntity;
		private boolean closed;
		private HttpUriRequest request;
		private HttpResponse response;
		private int maxRetryTimes = 3;
		private int mRetryTimes = 0;
		private Header[] mHeaders;

		private DefaultHttpConnection(String url, boolean isHttpPost) throws IOException {
			try {
				if (isHttpPost) {
					request = new HttpPost(url);
				} else {
					request = new HttpGet(url);
				}
			} catch (RuntimeException e) {
				throw new IOException("URISyntaxException in HttpUriRequest, post=" + isHttpPost + ", url=" + url);
			}
			synchronized (lock) {
				numOpenConnection++;
			}
		}

		private HttpResponse getResponse() throws IOException {
			if (response != null) {
				return response;
			}

			final YiYouHttpClient lClient = mClient;
			try {
				LogX.i(this, "via proxy : " + lClient.isViaProxy());

				if (mHeaders != null) {
					request.setHeaders(mHeaders);
				}

				// 设置请求的entity
				if (mHttpEntity != null) {
					((HttpPost) request).setEntity(mHttpEntity);
					if (mHttpEntity instanceof MultipartEntity) {
						LogX.d(this, "request entity : Multipart form entity does not implement #getContent(), So you don't see it ");
					} else {
						LogX.d(this, "request entity : " + EntityUtils.toString(((HttpPost) request).getEntity()));
					}
				} else if (baos != null) {
					ByteArrayEntity entity = new ByteArrayEntity(baos.toByteArray());
					((HttpPost) request).setEntity(entity);
					LogX.d(this, "request entity : " + EntityUtils.toString(((HttpPost) request).getEntity()));
				}

				if (lClient.isViaProxy()) {
					final URI uri = request.getURI();
					final String schmeme = uri.getScheme();
					HttpHost target = new HttpHost(uri.getHost(), lClient.getConnectionManager().getSchemeRegistry().getScheme(schmeme)
							.getDefaultPort(), schmeme);
					// google Issue 2690:
					// Apache HttpClient problem with https via http proxy
					response = lClient.execute(target, request);
				} else {
					response = lClient.execute(request);
				}

				mRetryTimes = 0;
			} catch (SocketException e) {
				if (e.getMessage().contains("Broken pipe") && mRetryTimes++ < maxRetryTimes) {
					LogX.jw(this, new SocketException("--> Broken pipe retry times: " + mRetryTimes));
					return getResponse();
				} else {
					throw new IOException("Broken pipe");
				}
			} catch (IOException e) {
				throw e;
			} catch (Exception e) {
				tryToRecover();
				throw new IOException(e.getClass().getName());
			}
			return response;
		}

		/**
		 * 设置请求消息头
		 * 
		 * @param mHeaders
		 */
		@Override
		public void setHeads(Header[] mHeaders) {
			this.mHeaders = mHeaders;
		}

		/**
		 * 系统横竖屏时抛出异常，如：IllegalStateException异常
		 */
		private void tryToRecover() {
			mClient.close();
			mClient = null;
			ensureClient();
		}

		@Override
		public void close() throws IOException {
			synchronized (lock) {
				try {
					if (response != null && response.getEntity() != null) {
						response.getEntity().consumeContent();
					}
				} catch (Exception e) {
					StringBuilder sb = new StringBuilder().append("Connection closed.  # of open connections=").append(numOpenConnection);
					throw new IOException(sb.toString());
				} finally {
					if (!closed) {
						closed = true;
						numOpenConnection--;
					}
				}

			}
		}

		@Override
		public String getContentType() throws IOException {
			Header header = getResponse().getEntity().getContentType();
			String contentType = "";
			if (header != null) {
				contentType = header.getValue();
			}
			return contentType;
		}

		@Override
		public String getHeaderField(int index) throws IOException {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getHeaderField(String key) throws IOException {
			Header header = getResponse().getFirstHeader(key);
			String field = "";
			if (header != null) {
				field = header.getValue();
			}
			return field;
		}

		@Override
		public String getHeaderFieldKey(int index) throws IOException {
			throw new UnsupportedOperationException();
		}

		@Override
		public long getLength() throws IOException {
			return getResponse().getEntity().getContentLength();
		}

		@Override
		public String getProtocolName() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getResponseCode() throws IOException {
			try {
				final HttpResponse response = getResponse();
				if (response != null && response.getStatusLine() != null) {
					return getResponse().getStatusLine().getStatusCode();
				}
			} catch (UnknownHostException e) {
				return HTTP_UNKNOW_HOST;
			}
			return -1;
		}

		@Override
		public String getResponseMessage() throws IOException {
			return FunctionUtils.inputStream2String(openDataInputStream());
		}

		@Override
		public boolean isHttps() {
			return "https".equals(request.getURI().getScheme());
		}

		@Override
		public void notifyTimeout() {
		}

		@Override
		public HttpEntity getResponseEntity() throws IOException {
			return getResponse().getEntity();
		}

		@Override
		public DataInputStream openDataInputStream() throws IOException {
			InputStream localInputStream = getResponse().getEntity().getContent();
			return new DataInputStream(localInputStream);
		}

		@Override
		public DataOutputStream openDataOutputStream() throws IOException {
			if (!(request instanceof HttpPost)) {
				StringBuilder localStringBuilder = new StringBuilder().append("Can't open output stream on a GET to ").append(request.getURI());
				throw new IOException(localStringBuilder.toString());
			}
			// 创建输出流对象
			this.baos = new ByteArrayOutputStream();
			return new DataOutputStream(baos);
		}

		@Override
		public void setEntity(List<NameValuePair> nvps) throws IOException {

			if (!(request instanceof HttpPost)) {
				StringBuilder localStringBuilder = new StringBuilder().append("Can't open output stream on a GET to ").append(request.getURI());
				throw new IOException(localStringBuilder.toString());
			}
			for (final NameValuePair parameter : nvps) {
				if (parameter instanceof NameFilePair) {
					setMultipartEntity(nvps);
					return;
				} else if (parameter.getName().equals("MultipartEntity")) {
					nvps.remove(parameter);
					setMultipartEntity(nvps);
					return;
				}
			}
			setUrlEncodedFormEntity(nvps);
		}

		public void setUrlEncodedFormEntity(List<NameValuePair> nvps) throws IOException {
			LogX.d(this, "setUrlEncodedFormEntity");
			mHttpEntity = new UrlEncodedFormEntity(nvps, HTTP.UTF_8);
		}

		public void setMultipartEntity(List<NameValuePair> nvps) throws IOException {
			LogX.d(this, "setMultipartEntity");
			mHttpEntity = new MultipartEntity();
			final String encoding = HTTP.UTF_8;
			for (final NameValuePair parameter : nvps) {
				final String encodedName = URLEncoder.encode(parameter.getName(), encoding);
				if (parameter instanceof NameFilePair) {
					((MultipartEntity) mHttpEntity).addPart(encodedName, new FileBody(((NameFilePair) parameter).getFile()));
				} else {
					final String value = parameter.getValue();
					final String encodedValue = value != null ? (URLEncoder.encode(value, encoding)) : "";
					((MultipartEntity) mHttpEntity).addPart(encodedName, new StringBody(encodedValue, Charsets.UTF_8));
				}
			}
		}

		@Override
		public void setEntity(JSONArray jsonArray) throws IOException {
			LogX.d(this, "setJsonEntity");
			mHttpEntity = new JsonEntity(jsonArray);
		}

		@Override
		public void setConnectionProperty(String name, String value) throws IOException {
			// 禁止设置 Content-Length 和 Transfer-Encoding属性
			if (("Content-Length".equals(name)) || ("Transfer-Encoding".equals(name))) {
				return;
			}
			request.setHeader(name, value);
		}

		/**
		 * 解析HTTP错误码
		 * 
		 * @param statusCode
		 * @return
		 */
		private String getCause(int statusCode) {
			String cause = null;
			switch (statusCode) {
			case HTTP_NOT_MODIFIED:
				break;
			case HTTP_BAD_REQUEST:
				cause = "The request was invalid.  An accompanying error message will explain why. This is the status code will be returned during rate limiting.";
				break;
			case HTTP_UNAUTHORIZED:
				cause = "Authentication credentials were missing or incorrect.";
				break;
			case HTTP_FORBIDDEN:
				cause = "The request is understood, but it has been refused.  An accompanying error message will explain why.";
				break;
			case HTTP_NOT_FOUND:
				cause = "The URI requested is invalid or the resource requested, such as a user, does not exists.";
				break;
			case HTTP_UNACCEPTABLE:
				cause = "Http request is unacceptable.";
				break;
			case HTTP_INTERNAL_ERROR:
				cause = "Http server internal error.";
				break;
			case HTTP_BAD_GATEWAY:
				cause = "Http bad gateway error.";
				break;
			case HTTP_SERVICE_UNAVAILABLE:
				cause = "Service Unavailable: The servers are up, but overloaded with requests. Try again later.";
				break;
			case HTTP_MOVED_TEMP:
				cause = "Your may not login your proxy server.";
				break;
			case HTTP_UNKNOW_HOST:
				cause = "Check your physical connection. Also it may come out when use android emulator send many many requests, just restart the emulator may solve the problem.";
				break;
			default:
				cause = "";
			}
			return statusCode + ":" + cause;
		}

		/**
		 * Handle Status code
		 * 
		 * @param statusCode 响应的状态码
		 * @throws HttpException 当响应码不为200时都会报出此异常:<br />
		 *             <li>HttpRequestException, 通常发生在请求的错误,如请求错误了 网址导致404等, 抛出此异常, 首先检查request log, 确认不是人为错误导致请求失败</li> <li>
		 *             HttpAuthException, 通常发生在Auth失败, 检查用于验证登录的用户名/密码/KEY等</li> <li>HttpRefusedException, 通常发生在服务器接受到请求, 但拒绝请求, 可是多种原因, 具体原因
		 *             服务器会返回拒绝理由, 调用HttpRefusedException#getError#getMessage查看</li> <li>
		 *             HttpServerException, 通常发生在服务器发生错误时, 检查服务器端是否在正常提供服务</li> <li>HttpException, 其他未知错误.</li>
		 */
		@Override
		public void handleResponseStatusCode(int statusCode) throws IOException {
			String msg = getCause(statusCode);

			switch (statusCode) {
			case HTTP_OK:
				break;

			case HTTP_UNKNOW_HOST:
				throw new IOException(msg);

			case HTTP_NOT_MODIFIED:
			case HTTP_BAD_REQUEST:
			case HTTP_NOT_FOUND:
			case HTTP_UNACCEPTABLE:
				throw new IOException(msg);

			case HTTP_UNAUTHORIZED:
				throw new IOException(msg);

			case HTTP_FORBIDDEN:
				throw new IOException(msg);

			case HTTP_INTERNAL_ERROR:
			case HTTP_BAD_GATEWAY:
			case HTTP_SERVICE_UNAVAILABLE:
				throw new IOException(msg);

			default:
				throw new IOException(msg);
			}
		}
	}
}
