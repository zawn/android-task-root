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
package com.taveloper.http.test.pojo.parse;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.taveloper.http.json.JsonReaderable;
import com.taveloper.http.test.StreamParseUtil;
import com.taveloper.http.test.pojo.Activity;
import java.io.IOException;

/**
 *
 * @author ZhangZhenli <Taveloper@gmail.com>
 */
public class ActivityParse implements JsonReaderable<Activity> {

    public Activity readJson(JsonParser in) throws JsonParseException, IOException {
//        System.out.println("ActivityParse.readJson");
        JsonToken curToken = in.nextToken();
        Activity object = new Activity();
        while (curToken == JsonToken.FIELD_NAME) {
            String curName = in.getText();
            JsonToken nextToken = in.nextToken();
            if ("url".equals(curName)) {
                object.setUrl(in.getText());
            } else if ("object".equals(curName)) {
                ActivityObjectParse activityObjectParse = new ActivityObjectParse();
                object.setActivityObject(activityObjectParse.readJson(in));
            }
            curToken = in.nextToken();
        }
        return object;
    }
}
