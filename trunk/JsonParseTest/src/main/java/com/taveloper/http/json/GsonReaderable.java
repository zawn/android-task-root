/*
 * Copyright 2013 ZhangZhenli <zhangzhenli@live.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.taveloper.http.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.google.gson.stream.JsonReader;
import java.io.IOException;

/**
 * 类通过实现 {@link JsonReaderable } 接口以启用其Json反序列化功能。
 *
 * @author ZhangZhenli
 */
public interface GsonReaderable<T> {

    /**
     * 从输入流 {@code Reader} 中读取Json格式的数据恢复对象
     *
     * @param in Json输入流
     */
    public T readJson(JsonReader in)throws IOException;
}
