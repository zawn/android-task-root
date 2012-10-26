package cn.mimessage.and.sdk.net.parser;

import org.apache.http.HttpEntity;

public abstract class Parser {
	public abstract void parser(HttpEntity entity, Object... bindObj) throws Exception;

	public void parserError(int errorCode, String why, Object... bindObj) {
	}
}
