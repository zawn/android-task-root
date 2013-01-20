/*
 * Copyright 2013 ZhangZhenli <Taveloper@gmail.com>.
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
package com.taveloper.http.test;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.taveloper.http.json.JsonReaderable;
import com.taveloper.http.test.pojo.ActivityFeed;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author ZhangZhenli <Taveloper@gmail.com>
 */
public class JsonParseManual {

    private final JsonParser parser;

    JsonParseManual(JsonParser parser) {
        this.parser = parser;
    }

    public ActivityFeed parseManual(InputStream bais, JsonReaderable readerable) throws IOException {
        StreamParseUtil.startParsing(parser);
        JsonToken currentToken = parser.getCurrentToken();
        if (currentToken == JsonToken.START_OBJECT) {
//            System.out.println("JsonToken.START_OBJECT");
            return (ActivityFeed) readerable.readJson(parser);
        } else if (currentToken == JsonToken.START_ARRAY) {
            System.out.println("JsonToken.START_ARRAY");
        }
        bais.close();
        return null;
    }

    
}
