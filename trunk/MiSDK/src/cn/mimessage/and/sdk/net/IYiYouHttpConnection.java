package cn.mimessage.and.sdk.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.entity.mime.JsonEntity;
import org.json.JSONArray;

public interface IYiYouHttpConnection {
	public static final String HEADER_KEY_ACCEPT = "Accept";
	public static final String HEADER_KEY_ACCEPT_CHARSET = "Accept-Charset";
	public static final String HEADER_KEY_CONTENT_LENGTH = "Content-Length";
	public static final String HEADER_KEY_CONTENT_TYPE = "Content-Type";
	public static final String HEADER_KEY_COOKIE = "Cookie";
	public static final String HEADER_KEY_LOCATION = "Location";
	public static final String HEADER_KEY_SET_COOKIE = "Set-Cookie";
	public static final String HEADER_KEY_USER_AGENT = "User-Agent";
	public static final int HTTP_UNKNOW_HOST = 99;
	public static final int HTTP_OK = 200;
	public static final int HTTP_CREATED = 201;
	public static final int HTTP_NO_CONTENT = 204;
	public static final int HTTP_MULTI_STATUS = 207;
	public static final int HTTP_MOVED_PERM = 301;
	public static final int HTTP_MOVED_TEMP = 302;
	public static final int HTTP_SEE_OTHER = 303;
	public static final int HTTP_NOT_MODIFIED = 304;
	public static final int HTTP_TEMP_REDIRECT = 307;
	public static final int HTTP_BAD_REQUEST = 400;
	public static final int HTTP_UNAUTHORIZED = 401;
	public static final int HTTP_FORBIDDEN = 403;
	public static final int HTTP_NOT_FOUND = 404;
	public static final int HTTP_UNACCEPTABLE = 406;
	public static final int HTTP_REQUEST_TIMEOUT = 408;
	public static final int HTTP_CONFLICT = 409;
	public static final int HTTP_LENGTH_REQUIRED = 411;
	public static final int HTTP_REQUEST_TOO_LARGE = 413;
	public static final int HTTP_UNSUPPORTED_MEDIA_TYPE = 415;
	public static final int HTTP_INTERNAL_ERROR = 500;
	public static final int HTTP_NOT_IMPLEMENTED = 501;
	public static final int HTTP_BAD_GATEWAY = 502;
	public static final int HTTP_SERVICE_UNAVAILABLE = 503;
	public static final String METHOD_GET = "GET";
	public static final String METHOD_HEAD = "HEAD";
	public static final String METHOD_POST = "POST";

	public void close() throws IOException;

	public String getContentType() throws IOException;

	public String getHeaderField(int index) throws IOException;

	public String getHeaderField(String name) throws IOException;

	public String getHeaderFieldKey(int index) throws IOException;

	public long getLength() throws IOException;

	public String getProtocolName();

	public int getResponseCode() throws IOException;

	public String getResponseMessage() throws IOException;

	public void handleResponseStatusCode(int statusCode) throws IOException;

	public boolean isHttps();

	public void notifyTimeout();

	public HttpEntity getResponseEntity() throws IOException;

	public DataInputStream openDataInputStream() throws IOException;

	public DataOutputStream openDataOutputStream() throws IOException;

	public void setEntity(List<NameValuePair> nvps) throws IOException;

	public void setEntity(JSONArray jsonArray) throws IOException;

	public void setConnectionProperty(String header, String value) throws IOException;

	public void setHeads(Header[] mHeaders);
}
