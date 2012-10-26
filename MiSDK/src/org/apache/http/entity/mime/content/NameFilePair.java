/*
 * Name   NameFilePair.java
 * Author ZhangZhenli
 * Created on 2012-8-3, 下午4:16:44
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package org.apache.http.entity.mime.content;

import java.io.File;

import org.apache.http.NameValuePair;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.LangUtils;

/**
 * 
 * @author ZhangZhenli
 */
public class NameFilePair implements NameValuePair {

	private final String name;
	private final File file;

	/**
	 * Default Constructor taking a name and a file. The file may be null.
	 * 
	 * @param name
	 *            The name.
	 * @param file
	 *            The file.
	 */
	public NameFilePair(final String name, final File file) {
		super();
		if (name == null) {
			throw new IllegalArgumentException("Name may not be null");
		}
		this.name = name;
		if (!file.exists()) {
			throw new IllegalArgumentException("File may not be exist");
		}
		this.file = file;
	}

	/**
	 * Returns the name.
	 * 
	 * @return String name The name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the file path.
	 * 
	 * @return String file The current file.
	 */
	public String getValue() {
		return this.file.getPath();
	}

	/**
	 * Returns the file.
	 * 
	 * @return File The current file.
	 */
	public File getFile() {
		return this.file;
	}

	/**
	 * Get a string representation of this pair.
	 * 
	 * @return A string representation.
	 */
	public String toString() {
		// don't call complex default formatting for a simple toString

		int len = this.name.length();
		if (this.file != null)
			len += 1 + this.file.length();
		CharArrayBuffer buffer = new CharArrayBuffer(len);

		buffer.append(this.name);
		if (this.file != null) {
			buffer.append("=");
			buffer.append(this.file.getPath());
		}
		return buffer.toString();
	}

	public boolean equals(final Object object) {
		if (object == null)
			return false;
		if (this == object)
			return true;
		if (object instanceof NameValuePair) {
			NameFilePair that = (NameFilePair) object;
			return this.name.equals(that.name)
					&& LangUtils.equals(this.file, that.file);
		} else {
			return false;
		}
	}

	public int hashCode() {
		int hash = LangUtils.HASH_SEED;
		hash = LangUtils.hashCode(hash, this.name);
		hash = LangUtils.hashCode(hash, this.file);
		return hash;
	}
}