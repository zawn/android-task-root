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
import com.taveloper.http.test.pojo.ActivityFeed;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author ZhangZhenli <Taveloper@gmail.com>
 */
public class ActivityFeedParse implements JsonReaderable<ActivityFeed> {

    public ActivityFeed readJson(JsonParser in) throws JsonParseException, IOException {
//        System.out.println("ActivityFeedParse.readJson");
        JsonToken curToken = in.nextToken();
        ActivityFeed object = new ActivityFeed();
        while (curToken == JsonToken.FIELD_NAME) {
            String curName = in.getText();
            JsonToken nextToken = in.nextToken();
            if ("items".equals(curName)) {
                ArrayList<Activity> arrayList = new ArrayList<Activity>();
                ActivityParse activityParse = new ActivityParse();
                switch (nextToken) {
                    case START_ARRAY:
                        while (in.nextToken() != JsonToken.END_ARRAY) {
                            arrayList.add(activityParse.readJson(in));
                        }
                        break;
                    case START_OBJECT:
                        arrayList.add(activityParse.readJson(in));
                        break;
                    default:
                        throw new IllegalArgumentException("unexpected JSON node type: " + nextToken + in.getCurrentName());
                }
                object.setActivities(arrayList);
            }
            curToken = in.nextToken();
        }
        return object;
    }
}
