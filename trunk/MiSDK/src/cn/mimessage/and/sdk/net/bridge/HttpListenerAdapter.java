package cn.mimessage.and.sdk.net.bridge;

import org.apache.http.HttpEntity;

import cn.mimessage.and.sdk.net.parser.Parser;

public abstract class HttpListenerAdapter implements IHttpListener {
	public static final int PARSE_ERROR = -1000;
	private Parser mParser;

	public void setParser(Parser parser) {
		mParser = parser;
	}

	@Override
	public void onHttpFailure(int errorCode, String why, Object... bindObj) {
		if (mParser != null) {
			mParser.parserError(errorCode, why, bindObj);
		}
	}

	@Override
	public void onHttpSuccess(HttpEntity entity, Object... bindObj) {
		if (mParser != null) {
			try {
				mParser.parser(entity, bindObj);
			} catch (Exception e) {
				onHttpFailure(PARSE_ERROR, "PARSE ERROR : " + mParser.getClass().getName());
				e.printStackTrace();
			}
		}
	}
}
