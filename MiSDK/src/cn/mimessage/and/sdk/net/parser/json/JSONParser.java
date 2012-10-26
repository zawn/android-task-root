package cn.mimessage.and.sdk.net.parser.json;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.json.JSONException;

import cn.mimessage.and.sdk.net.parser.Parser;

public abstract class JSONParser extends Parser {
	public static final int JSON_PARSE_ERROR = -1001;

	@Override
	public abstract void parser(HttpEntity entity, Object... bindObj) throws JSONException, ParseException, IOException;
}
