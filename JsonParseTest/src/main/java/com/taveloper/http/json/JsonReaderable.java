/*
 * Name   JsonReaderable.java
 * Author ZhangZhenli
 * Created on 2012-10-31, 下午3:50:00
 *
 * Copyright (c) 2012 NanJing YiWuXian Network Technology Co., Ltd. All rights reserved
 *
 */
package com.taveloper.http.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import java.io.IOException;

/**
 * 类通过实现 {@link JsonReaderable } 接口以启用其Json反序列化功能。
 *
 * @author ZhangZhenli
 */
public interface JsonReaderable<T> {

    /**
     * 从输入流 {@code Reader} 中读取Json格式的数据恢复对象
     *
     * @param in Json输入流
     */
    public T readJson(JsonParser in) throws JsonParseException, IOException;
}
