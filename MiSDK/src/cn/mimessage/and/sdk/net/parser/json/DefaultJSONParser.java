package cn.mimessage.and.sdk.net.parser.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.mimessage.and.sdk.util.log.LogX;

/**
 * 默认的JSON解析器 ,本类假设:<li>用户知道JSON数据中各值对应的名称</li> <li>
 * 用户知道JSON数据中名称对应各值的数据结构</li>
 * 
 * @author 11050160
 * 
 * @param <T> 解析完成后需要初始化的对象
 */
public class DefaultJSONParser extends JSONParser {
	/**
	 * 最终JSON数据对象
	 */
	private Map<String, JSONDataHolder> mJsonParcel;

	protected IJSONListener mListener;
	public static JSONObject mSalesDetailQueryJson;

	private static final int RECOVER_LEVEL_NORMAL = 0;
	private static final int RECOVER_DEAD = 13;

	private class JSONDoctor {
		int recoverLevel;
		String entity;

		String cureIllnessString() {
			final String content = entity;
			if (content == null || content.length() == 0) {
				LogX.je(this, new Throwable("JSON entity is null or empty when cureIllnessString()"));
				return content;
			}

			char a = '\n';
			char tmp = 0;
			StringBuffer sb = new StringBuffer();

			int lastPos = 0;
			int i = 0;

			while (i < content.length()) {
				tmp = content.charAt(i);
				if (tmp == a) {
					if (lastPos < i) {
						sb.append(content.substring(lastPos, i));
					}
					lastPos = i + 1;
				}
				i++;
			}
			if (lastPos < content.length()) {
				sb.append(content.substring(lastPos, content.length() - 1));
			}
			return sb.toString();
		}
	}

	private JSONDoctor mDoctor;

	public DefaultJSONParser(IJSONListener listener) {
		mListener = listener;
		mJsonParcel = new HashMap<String, JSONDataHolder>();
		mDoctor = null;
	}

	public synchronized Map<String, JSONDataHolder> getParcel() {
		return mJsonParcel;
	}

	@Override
	public synchronized void parser(HttpEntity entity, Object... bindObj) throws JSONException, ParseException, IOException {
		if (entity == null) {
			throw new IOException("The entity object is null when parse HttpEntity");
		}

		String entityContent = null;
		try {
			final Map<String, JSONDataHolder> jsonParcel = mJsonParcel;
			final JSONDoctor doctor = mDoctor;
			if (mDoctor == null) {
				if (jsonParcel == null) {
					throw new IOException("The data holder object is null when parse HttpEntity");
				}

				entityContent = EntityUtils.toString(entity);

				if ("".equals(entityContent)) {
					throw new ParseException("The entity content is empty.");
				}
			} else {
				entityContent = doctor.cureIllnessString();
			}
			if (entityContent.contains("orderDiscount")) {
				String tmp = entityContent.substring(0, entityContent.length() - 2);
				tmp = tmp.trim();
				tmp += "\"}";
				entityContent = tmp;
			}

			JSONObject json = null;
			try {
				json = new JSONObject(entityContent);
			} catch (JSONException e) {
				if (entityContent.length() == 1) {
					json = new JSONObject("{resultCode:" + entityContent + "}");
				} else {
					e.printStackTrace();
				}
			}
			mSalesDetailQueryJson = json;
			LogX.d(this, "json : " + json.toString());

			if (json != null) {
				jsonParcel.clear();
			}

			buildJSONMap(mJsonParcel, json);

			parseOver(jsonParcel, bindObj);

			mDoctor = null;
		} catch (JSONException e) {
			if (entityContent == null || entityContent.length() <= 0) {
				throw e;
			}

			JSONDoctor doctor = mDoctor;
			if (doctor == null) {
				doctor = new JSONDoctor();
				doctor.recoverLevel = RECOVER_LEVEL_NORMAL;
				mDoctor = doctor;
				doctor.entity = entityContent;
			}

			switch (doctor.recoverLevel) {
			case RECOVER_LEVEL_NORMAL:
				doctor.recoverLevel = RECOVER_DEAD;
				parser(entity);
				break;
			case RECOVER_DEAD:
				// 没救了
			default:
				throw e;
			}
		} finally {
			if (entity != null) {
				entity.consumeContent();
			}

			mDoctor = null;
		}
	}

	/**
	 * JSON 对象中数据存储对象
	 * 
	 * @author 11050160
	 * 
	 */
	public static class JSONDataHolder {
		private int mIntValue;
		private String mStrValue;
		private long mLongValue;
		private List<Map<String, JSONDataHolder>> mList;

		public JSONDataHolder(Object object) throws JSONException {
			if (object instanceof String) {
				mStrValue = (String) object;
			} else if (object instanceof JSONArray) {
				mList = new ArrayList<Map<String, JSONDataHolder>>();
				buildList((JSONArray) object);
			} else if (object instanceof Integer) {
				mIntValue = ((Integer) object).intValue();
			} else if (object instanceof Long) {
				mLongValue = ((Long) object).longValue();
			}
		}

		public int getInt() {
			return mIntValue;
		}

		public long getLong() {
			return mLongValue;
		}

		public String getString() {
			return mStrValue;
		}

		public List<Map<String, JSONDataHolder>> getList() {
			return mList;
		}

		private void buildList(JSONArray array) throws JSONException {
			JSONObject json;
			int count = array.length();
			for (int i = 0; i < count; i++) {
				json = array.getJSONObject(i);
				Map<String, JSONDataHolder> map = new HashMap<String, JSONDataHolder>();
				buildJSONMap(map, json);
				mList.add(map);
			}
		}
	}

	private static void buildJSONMap(Map<String, JSONDataHolder> map, JSONObject json) throws JSONException {
		@SuppressWarnings("unchecked")
		// Using legacy API
		Iterator<String> it = json.keys();
		String key;
		while (it.hasNext()) {
			key = it.next();
			map.put(key, new JSONDataHolder(json.get(key)));
		}
	}

	protected void parseOver(Map<String, JSONDataHolder> jsonParcel, Object... bindObj) {
		if (mListener != null) {
			((IJSONParseOverListener) mListener).onParseOver(jsonParcel);
		}
	}

	@Override
	public void parserError(int errorCode, String why, Object... bindObj) {
		if (mListener != null) {
			((IJSONParseOverListener) mListener).parserJSONError(errorCode, why);
		}
	}
}
