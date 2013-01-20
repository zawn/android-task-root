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

import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.taveloper.http.json.GsonReaderable;
import com.taveloper.http.test.pojo.Activity;
import com.taveloper.http.test.pojo.ActivityFeed;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author ZhangZhenli <Taveloper@gmail.com>
 */
public class ActivityFeedParse implements GsonReaderable<ActivityFeed> {

    public ActivityFeed readJson(JsonReader in) throws IOException {
//        System.out.println("ActivityFeedParse.readJson");
        in.beginObject();
        JsonToken curToken = in.peek();
        ActivityFeed object = new ActivityFeed();
        while (curToken == JsonToken.NAME) {
            String curName = in.nextName();
            JsonToken nextToken = in.peek();
            if ("items".equals(curName)) {
                ArrayList<Activity> arrayList = new ArrayList<Activity>();
                ActivityParse activityParse = new ActivityParse();
                switch (nextToken) {
                    case BEGIN_ARRAY:
                        in.beginArray();
                        while (in.peek() != JsonToken.END_ARRAY) {
                            arrayList.add(activityParse.readJson(in));
                        }
                        in.endArray();
                        break;
                    case BEGIN_OBJECT:
                        arrayList.add(activityParse.readJson(in));
                        break;
                    default:
                        throw new IllegalArgumentException("unexpected JSON node type: " + nextToken + in.nextName());
                }
                object.setActivities(arrayList);
            } else {
                in.skipValue();
            }
            curToken = in.peek();
        }
        in.endObject();
        return object;
    }
}
