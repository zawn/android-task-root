package cn.mimessage.and.sdk.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import android.util.Log;
import cn.mimessage.and.sdk.util.log.LogX;

public final class YiYouHttpClient implements HttpClient {
	public static long DEFAULT_SYNC_MIN_GZIP_BYTES = 0L;
	private static boolean viaProxy;
	private static HttpHost mProxyHost;
	private static final ThreadLocal<Boolean> sThreadBlocked = new ThreadLocal<Boolean>();
	private volatile LoggingConfiguration curlConfiguration;
	private final HttpClient delegate;
	private HttpContext delegateContext;
	private RuntimeException mLeakedException;
	private static final String TAG = "YiYouHttpClient_v01";

	private static final HttpRequestInterceptor sThreadCheckInterceptor = new HttpRequestInterceptor() {
		@Override
		public void process(HttpRequest request, HttpContext context) {
			if (sThreadBlocked.get() != null && sThreadBlocked.get().booleanValue())
				throw new RuntimeException("This thread forbids HTTP requests");
		}
	};
	private static final int RETRIED_TIME = 3;
	/**
	 * 异常自动恢复处理, 使用HttpRequestRetryHandler接口实现请求的异常恢复
	 */
	private static HttpRequestRetryHandler requestRetryHandler = new HttpRequestRetryHandler() {
		// 自定义的恢复策略
		@Override
		public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
			LogX.e(TAG, "retrying : " + exception.getClass().getName());
			// 设置恢复策略，在发生异常时候将自动重试N次
			if (executionCount >= RETRIED_TIME) {
				// Do not retry if over max retry count
				return false;
			}
			if (exception instanceof NoHttpResponseException) {
				// Retry if the server dropped connection on us
				return true;
			}
			if (exception instanceof SSLHandshakeException) {
				// Do not retry on SSL handshake exception
				return false;
			}
			HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
			boolean idempotent = (request instanceof HttpEntityEnclosingRequest);
			if (!idempotent) {
				// Retry if the request is considered idempotent
				return true;
			}
			return false;
		}
	};

	/**
	 * 私有构造函数
	 * 
	 * @param paramClientConnectionManager 链接管理器
	 * @param paramHttpParams http参数
	 */
	private YiYouHttpClient(ClientConnectionManager connManager, HttpParams params) {
		mLeakedException = new IllegalStateException(TAG + " created and never closed");
		if (delegateContext == null) {
			delegateContext = new BasicHttpContext();
		}
		DefaultHttpClient httpClient = new CoreHttpClient(connManager, params, delegateContext);
		httpClient.setHttpRequestRetryHandler(requestRetryHandler);
		delegate = httpClient;
	}

	/**
	 * 获取GZIP压缩后的Entity
	 * 
	 * @param data 数据字节
	 * @param resolver 数据共享对象
	 * @return
	 * @throws IOException
	 */
	public static AbstractHttpEntity getCompressedEntity(byte[] data) throws IOException {
		// 有效性检查
		if (data.length < getMinGzipSize()) {
			return null;
		}

		ByteArrayEntity localByteArrayEntity;

		// 进行GZIP压缩
		ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
		GZIPOutputStream localGZIPOutputStream = new GZIPOutputStream(localByteArrayOutputStream);
		localGZIPOutputStream.write(data);
		localGZIPOutputStream.close();
		// GZIP流转换成字节
		byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();

		// 生成Entity对象
		localByteArrayEntity = new ByteArrayEntity(arrayOfByte);
		localByteArrayEntity.setContentEncoding("gzip");
		return localByteArrayEntity;
	}

	/**
	 * 获取最小GZIP大小
	 * 
	 * @param resolver 数据共享对象
	 * @return
	 */
	public static long getMinGzipSize() {
		return DEFAULT_SYNC_MIN_GZIP_BYTES;
	}

	/**
	 * 获取解压缩后的内容
	 * 
	 * @param entity 请求response的entity对象
	 * @return
	 * @throws IOException
	 */
	public static InputStream getUngzippedContent(HttpEntity entity) throws IOException {
		InputStream zippedInputStream = entity.getContent();
		if (zippedInputStream == null) {
			return null;
		}

		Header header = entity.getContentEncoding();
		if (header == null) {
			return null;
		}

		String headStr = header.getValue();
		if ((headStr == null) || (!headStr.contains("gzip"))) {
			return null;
		}

		zippedInputStream = new GZIPInputStream(zippedInputStream);
		return zippedInputStream;
	}

	/**
	 * 设置请求属性为GZIP编码
	 * 
	 * @param request http请求
	 */
	public static void modifyRequestToAcceptGzipResponse(HttpRequest request) {
		request.addHeader("Accept-Encoding", "gzip");
	}

	/**
	 * 新建一个对象(多例模式)
	 * 
	 * @param userAgentParamString 终端用户类型
	 * @return
	 */
	public static YiYouHttpClient newInstance(String userAgentParamString) {
		BasicHttpParams params = new BasicHttpParams();
		ConnManagerParams.setMaxTotalConnections(params, 12);

		// 设置是否检查连接有效
		HttpConnectionParams.setStaleCheckingEnabled(params, false);
		// 设置链接超时时间20s(从发起连接到链接成功)
		HttpConnectionParams.setConnectionTimeout(params, 20000);
		// 设置socket链接超时时间20s(等待数据获取的时间)
		HttpConnectionParams.setSoTimeout(params, 20000);
		// 设置socket缓冲大小8K
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		// 设置是否重定向
		HttpClientParams.setRedirecting(params, false);
		// 设置终端用户类型
		HttpProtocolParams.setUserAgent(params, userAgentParamString);

		params.setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);

		if (viaProxy && mProxyHost != null) {
			// 设置代理
			params.setParameter(ConnRoutePNames.DEFAULT_PROXY, mProxyHost);
		}

		SchemeRegistry supportedSchemes = new SchemeRegistry();

		// Register the "http" and "https" protocol schemes, they are
		// required by the default operator to look up socket factories.
		supportedSchemes.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		// For 生产环境服务器
		// supportedSchemes.register(new Scheme("https", SSLSocketFactory
		// .getSocketFactory(), 443));

		// For PRE环境服务器
		// Issue 1946: javax.net.ssl.SSLException: Not trusted server
		// certificate
		// http://code.google.com/p/android/issues/detail?id=1946
		supportedSchemes.register(new Scheme("https", new EasySSLSocketFactory(), 443));
		// prepare parameters
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setUseExpectContinue(params, true);

		ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, supportedSchemes);

		// 配置生效
		return new YiYouHttpClient(ccm, params);
	}

	/**
	 * 阻塞当前线程
	 * 
	 * @param block
	 */
	public static void setThreadBlocked(boolean block) {
		sThreadBlocked.set(Boolean.valueOf(block));
	}

	private static String toCurl(HttpUriRequest request) throws IOException {
		// TODO 日志获取
		return null;
	}

	/**
	 * 使用代理服务器
	 * 
	 * @param host 主机名
	 * @param port 端口
	 */
	public void enableProxy(String host, int port) {
		enableProxy(new HttpHost(host, port));
	}

	public synchronized void enableProxy(HttpHost proxyHost) {
		if (delegate != null) {
			viaProxy = true;
			mProxyHost = proxyHost;
			final HttpParams params = delegate.getParams();
			params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyHost);
		}
	}

	/**
	 * 关闭代理
	 */
	public synchronized void disableProxy() {
		viaProxy = false;
		if (delegate != null) {
			final HttpParams params = delegate.getParams();
			params.setParameter(ConnRoutePNames.DEFAULT_PROXY, null);
		}
	}

	/**
	 * 是否使用代理
	 * 
	 * @return
	 */
	public synchronized boolean isViaProxy() {
		return viaProxy;
	}

	/**
	 * 释放连接
	 */
	public void close() {
		if (mLeakedException != null) {
			getConnectionManager().shutdown();
			mLeakedException = null;
		}
	}

	/**
	 * 关闭日志功能
	 */
	public void disableCurlLogging() {
		curlConfiguration = null;
	}

	/**
	 * 打开日志功能
	 * 
	 * @param name
	 * @param level
	 */
	public void enableCurlLogging(String name, int level) {
		if (name == null)
			throw new NullPointerException("name");
		if ((level < 2) || (level > 7))
			throw new IllegalArgumentException("Level is out of range [2..7]");
		curlConfiguration = new LoggingConfiguration(level);
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.http.client.HttpClient#execute(org.apache.http.HttpHost, org.apache.http.HttpRequest, org.apache.http.client.ResponseHandler)
	 */
	@Override
	public <T> T execute(HttpHost host, HttpRequest request, ResponseHandler<? extends T> handler) throws IOException, ClientProtocolException {
		return delegate.execute(host, request, handler);
	}

	@Override
	public <T> T execute(HttpHost host, HttpRequest request, ResponseHandler<? extends T> handler, HttpContext context) throws IOException,
			ClientProtocolException {
		return delegate.execute(host, request, handler, context);
	}

	@Override
	public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> handler) throws IOException, ClientProtocolException {
		return delegate.execute(request, handler);
	}

	@Override
	public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> handler, HttpContext context) throws IOException,
			ClientProtocolException {
		return delegate.execute(request, handler, context);
	}

	@Override
	public HttpResponse execute(HttpHost host, HttpRequest request) throws IOException {
		return delegate.execute(host, request);
	}

	@Override
	public HttpResponse execute(HttpHost host, HttpRequest request, HttpContext context) throws IOException {

		return delegate.execute(host, request, context);
	}

	@Override
	public HttpResponse execute(HttpUriRequest request) throws IOException {
		return delegate.execute(request);
	}

	@Override
	public HttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException {
		return delegate.execute(request, context);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if (mLeakedException != null) {
			Log.e(TAG, "Leak found", mLeakedException);
			mLeakedException = null;
		}
	}

	@Override
	public ClientConnectionManager getConnectionManager() {
		return delegate.getConnectionManager();
	}

	@Override
	public HttpParams getParams() {
		return delegate.getParams();
	}

	/**
	 * 网络请求记录类
	 * 
	 * @author 11050160
	 * 
	 */
	class CurlLogger implements HttpRequestInterceptor {
		@Override
		public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
			final LoggingConfiguration localLoggingConfiguration = curlConfiguration;
			if (localLoggingConfiguration != null && localLoggingConfiguration.isLoggable() && (request instanceof HttpUriRequest)) {
				String str = toCurl((HttpUriRequest) request);
				localLoggingConfiguration.println(str);
			}
		}
	}

	/**
	 * 日志配置
	 * 
	 * @author 11050160
	 * 
	 */
	class LoggingConfiguration {
		private final int level;

		private LoggingConfiguration(int arg2) {
			level = 0;
		}

		private boolean isLoggable() {
			return false;
		}

		private void println(String paramString) {
			Log.println(level, TAG, paramString);
		}

	}

	/**
	 * 真实HttpClient对象
	 * 
	 */
	final class CoreHttpClient extends DefaultHttpClient {
		private HttpContext mHttpContext;

		public CoreHttpClient(ClientConnectionManager connManager, HttpParams params, HttpContext context) {
			super(connManager, params);
			if (context == null) {
				throw new RuntimeException("CoreHttpClient won't accept null HttpContext.");
			}
			mHttpContext = context;
			buildHttpContext();
		}

		@Override
		protected HttpContext createHttpContext() {
			return mHttpContext;
		}

		@Override
		protected BasicHttpProcessor createHttpProcessor() {
			final BasicHttpProcessor processor = super.createHttpProcessor();
			HttpRequestInterceptor httpRequestInterceptor = sThreadCheckInterceptor;
			processor.addRequestInterceptor(httpRequestInterceptor);
			CurlLogger curlLogger = new CurlLogger();
			processor.addRequestInterceptor(curlLogger);
			return processor;
		}

		private void buildHttpContext() {
			mHttpContext.setAttribute(ClientContext.AUTHSCHEME_REGISTRY, getAuthSchemes());
			mHttpContext.setAttribute(ClientContext.COOKIESPEC_REGISTRY, getCookieSpecs());
			mHttpContext.setAttribute(ClientContext.CREDS_PROVIDER, getCredentialsProvider());
			mHttpContext.setAttribute(ClientContext.COOKIE_STORE, getCookieStore());
		}
	}

}
