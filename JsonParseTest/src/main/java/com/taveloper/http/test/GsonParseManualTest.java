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
package com.taveloper.http.test;

import com.google.api.client.json.JsonParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import android.support.json.JsonReader;
import android.support.json.JsonToken;
import static com.taveloper.http.test.App.json;
import com.taveloper.http.test.pojo.Activity;
import com.taveloper.http.test.pojo.ActivityFeed;
import com.taveloper.http.test.pojo.gparse.ActivityFeedParse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author ZhangZhenli <zhangzhenli@live.com>
 */
public class GsonParseManualTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnsupportedEncodingException, IOException {
        ByteArrayInputStream bais2 = new ByteArrayInputStream(json.getBytes("UTF-8"));
        ActivityFeed parseAuto = parseAuto(bais2);
        for (int i = 0; i < 100000; i++) {
            StringReader reader = new StringReader(json);
            ActivityFeed parseResult = parseManual(reader);
//            if (!parseManual.equals(parseAuto)) {
//                System.out.println("No activities found.");
//            }
            System.out.println(i);
            if (parseResult.getActivities().isEmpty()) {
                System.out.println("No activities found.");
            } else {
//                for (Activity activity : parseResult.getActivities()) {
//                    System.out.println();
//                    System.out.println("-----------------------------------------------");
//                    System.out.println("HTML Content: " + activity.getActivityObject().getContent());
//                    System.out.println("+1's: " + activity.getActivityObject().getPlusOners().getTotalItems());
//                    System.out.println("URL: " + activity.getUrl());
//                    System.out.println("ID: " + activity.get("id"));
//                }
                
//                if (parseResult.equals(parseAuto)) {
//                    System.out.println("Yeah!!");
//                }
            }
        }
    }

    private static ActivityFeed parseManual(StringReader reader) throws IOException {
        JsonReader jsonReader = new JsonReader(reader);
        return gparseManual(jsonReader, new ActivityFeedParse());
    }

    static ActivityFeed gparseManual(JsonReader parser, ActivityFeedParse activityFeedParse) throws IOException {
        JsonToken currentToken = parser.peek();
        if (currentToken == JsonToken.BEGIN_OBJECT) {
//            System.out.println("JsonToken.START_OBJECT");
            return (ActivityFeed) activityFeedParse.readJson(parser);
        } else if (currentToken == JsonToken.BEGIN_ARRAY) {
//            System.out.println("JsonToken.START_ARRAY");
        }
        parser.close();
        return null;
    }

    private static ActivityFeed parseAuto(InputStream in) throws IOException {
        JacksonFactory factory = new JacksonFactory();
        JsonParser parser = factory.createJsonParser(in);
        ActivityFeed feed = parser.parseAndClose(ActivityFeed.class, null);
        if (feed.getActivities().isEmpty()) {
            System.out.println("No activities found.");
        } else {
            for (Activity activity : feed.getActivities()) {
                System.out.println();
                System.out.println("-----------------------------------------------");
                System.out.println("HTML Content: " + activity.getActivityObject().getContent());
                System.out.println("+1's: " + activity.getActivityObject().getPlusOners().getTotalItems());
                System.out.println("URL: " + activity.getUrl());
                System.out.println("ID: " + activity.get("id"));
            }
        }
        return feed;
    }
}
