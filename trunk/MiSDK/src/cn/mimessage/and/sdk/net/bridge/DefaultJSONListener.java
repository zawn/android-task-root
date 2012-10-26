package cn.mimessage.and.sdk.net.bridge;

import cn.mimessage.and.sdk.net.parser.json.DefaultJSONParser;
import cn.mimessage.and.sdk.net.parser.json.IJSONParseOverListener;

public class DefaultJSONListener extends HttpListenerAdapter {
	public DefaultJSONListener(IJSONParseOverListener listener) {
		setParser(new DefaultJSONParser(listener));
	}
}
