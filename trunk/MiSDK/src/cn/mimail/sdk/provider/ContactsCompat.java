/*
 * Name   ContactsCompat.java
 * Author ZhangZhenli
 * Created on 2012-10-14, 下午2:45:06
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package cn.mimail.sdk.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.Contacts;
import android.provider.Contacts.People;
import android.provider.ContactsContract;

/**
 * 
 * @author ZhangZhenli
 */
@SuppressWarnings("deprecation")
public class ContactsCompat {

	public static final int TYPE_HOME = 1;
	public static final int TYPE_MOBILE = 2;
	public static final int TYPE_WORK = 3;
	public static final int TYPE_FAX_WORK = 4;
	public static final int TYPE_FAX_HOME = 5;
	public static final int TYPE_PAGER = 6;
	public static final int TYPE_OTHER = 7;

	@SuppressWarnings("unused")
	private static final String TAG = "ContactsCompat.java";

	private static ContactsCompatImpl IMPL;

	static {
		if (Build.VERSION.SDK_INT >= 5) {
			IMPL = new ContactsCompatImplEclair();
		} else {
			IMPL = new ContactsCompatImplBase();
		}
	}

	/**
	 * 返回手机通讯录列表,每个联系人对应一个HashMap,每个HashMap中键name对应{@link String}类型的联系人的姓名,键phones对应{@link List}类型的电话列表,
	 * <br>每个电话为形如下的一维数组:
	 * <br>{number, type}
	 * <br> 其中number表示电话号码,type表示给号码的类型
	 * 
	 * @param mContext
	 * @return
	 */
	public static List<HashMap<String, Object>> getContacts(Context mContext) {
		return IMPL.getContacts(mContext);
	}

	/**
	 * Compatibility shims for sharing operations
	 */
	interface ContactsCompatImpl {
		public List<HashMap<String, Object>> getContacts(Context mContext);
	}

	@TargetApi(4)
	static class ContactsCompatImplBase implements ContactsCompatImpl {

		public List<HashMap<String, Object>> getContacts(Context mContext) {
			List<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();
			ContentResolver cr = mContext.getContentResolver();
			Cursor cCur = null;
			try {
				cCur = cr.query(People.CONTENT_URI, null, null, null, null);
				if (cCur.getCount() > 0) {
					int idColumn = cCur.getColumnIndex(People._ID);
//					int nameColumn = cCur.getColumnIndex(People.NAME);
					int displayNameColumn = cCur.getColumnIndex(People.DISPLAY_NAME);

					while (cCur.moveToNext()) {
						String id = cCur.getString(idColumn);
						String displayName = cCur.getString(displayNameColumn);
//						String name = cCur.getString(nameColumn);
						List<String[]> phoneNumbers = new ArrayList<String[]>();
						Cursor peopleCur = cr.query(Contacts.Phones.CONTENT_URI, null, Contacts.Phones.PERSON_ID + " = ?", new String[] { id }, null);
						int pCount = peopleCur.getCount();
						if (pCount > 0) {
							int numberColumn = peopleCur.getColumnIndex(Contacts.People.NUMBER);
							int typeColumn = peopleCur.getColumnIndex(Contacts.Phones.TYPE);
							while (peopleCur.moveToNext()) {
								phoneNumbers.add( new String[] { peopleCur.getString(numberColumn), peopleCur.getString(typeColumn) });
							}
						}
						peopleCur.close();
//						Log.i(TAG, "name:" + name + ",displayName:" + displayName + "," + Arrays.deepToString(phoneNumbers.toArray()));

						HashMap<String, Object> hashMap = new HashMap<String, Object>();
						hashMap.put("id", id);
						hashMap.put("name", displayName);
						hashMap.put("phones", phoneNumbers);
						items.add(hashMap);
					}
				}
			} finally {
				if (cCur != null) {
					cCur.close();
				}
			}
			return items;
		}
	}

	@TargetApi(5)
	static class ContactsCompatImplEclair implements ContactsCompatImpl {

		@Override
		public List<HashMap<String, Object>> getContacts(Context mContext) {
			List<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();
			Cursor cur = null;
			try {
				cur = mContext.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
				if (cur.moveToFirst()) {
					int idColumn = cur.getColumnIndex(BaseColumns._ID);
					int displayNameColumn = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
					// Iterate all users
					do {
						String contactId = cur.getString(idColumn);
						String displayName = cur.getString(displayNameColumn);
						List<String[]> phoneNumbers = new ArrayList<String[]>();
						int countColumn = cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
						int numberCount = cur.getInt(countColumn);
						if (numberCount > 0) {
							Cursor peopleCur = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
									ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId
									/*
									 * + " and " + ContactsContract.CommonDataKinds.Phone.TYPE + "=" +
									 * ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
									 */, null, null);
							if (peopleCur.moveToFirst()) {
								int numberColumn = peopleCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
								int typeColumn = peopleCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
								// Iterate all numbers
								do {
									phoneNumbers.add(new String[] { peopleCur.getString(numberColumn), peopleCur.getString(typeColumn) });
								} while (peopleCur.moveToNext());
							}
							peopleCur.close();
						}
//						Log.i(TAG, "displayName:" + displayName + "," + Arrays.deepToString(phoneNumbers.toArray()));

						HashMap<String, Object> i = new HashMap<String, Object>();
						i.put("id", contactId);
						i.put("name", displayName);
						i.put("phones", phoneNumbers);
						items.add(i);
					} while (cur.moveToNext());
				}
			} finally {
				if (cur != null)
					cur.close();
			}
			return items;
		}

	}
}
