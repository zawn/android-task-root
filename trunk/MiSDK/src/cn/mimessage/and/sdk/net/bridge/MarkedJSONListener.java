package cn.mimessage.and.sdk.net.bridge;

import cn.mimessage.and.sdk.net.parser.json.IMarkedJSONParseOverListener;
import cn.mimessage.and.sdk.net.parser.json.MarkedJSONParser;

public class MarkedJSONListener extends HttpListenerAdapter {
	public MarkedJSONListener(IMarkedJSONParseOverListener listener) {
		setParser(new MarkedJSONParser(listener));
	}
}
