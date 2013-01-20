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
package com.taveloper.http.test.pojo.gparse;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.taveloper.http.json.GsonReaderable;
import com.taveloper.http.test.pojo.ActivityObject;
import java.io.IOException;

/**
 *
 * @author ZhangZhenli <Taveloper@gmail.com>
 */
public class ActivityObjectParse implements GsonReaderable<ActivityObject> {

    public ActivityObject readJson(JsonReader in) throws IOException {
//        System.out.println("ActivityObjectParse.readJson");
        in.beginObject();
        JsonToken curToken = in.peek();
        ActivityObject object = new ActivityObject();
        while (curToken == JsonToken.NAME) {
            String curName = in.nextName();
            JsonToken nextToken = in.peek();
            if ("content".equals(curName)) {
                object.setContent(in.nextString());
            } else if ("plusoners".equals(curName)) {
                PlusOnersParse plusOnersParse = new PlusOnersParse();
                object.setPlusOners(plusOnersParse.readJson(in));
            }
            curToken = in.peek();
        }
        in.endObject();
        return object;
    }
}
