package cn.mimessage.and.sdk.net.parser.json;

import java.util.Map;

public class MarkedJSONParser extends DefaultJSONParser {

	public MarkedJSONParser(IMarkedJSONParseOverListener listener) {
		super(listener);
	}

	@Override
	protected void parseOver(Map<String, JSONDataHolder> jsonParcel, Object... bindObj) {
		if (mListener != null) {
			((IMarkedJSONParseOverListener) mListener).onParseOver(jsonParcel, bindObj);
		}
	}

	@Override
	public void parserError(int errorCode, String why, Object... bindObj) {
		if (mListener != null) {
			((IMarkedJSONParseOverListener) mListener).parserJSONError(errorCode, why, bindObj);
		}
	}
}
