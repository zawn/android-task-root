package cn.mimessage.and.sdk.net.parser.json;

import java.util.Map;

import cn.mimessage.and.sdk.net.parser.json.DefaultJSONParser.JSONDataHolder;

public interface IMarkedJSONParseOverListener extends IJSONListener {
	public void onParseOver(Map<String, JSONDataHolder> jsonParcel, Object... bindObj);

	public void parserJSONError(int errorCode, String why, Object... bindObj);
}
