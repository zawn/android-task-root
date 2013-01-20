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

import com.google.api.client.json.*;
import com.google.api.client.json.jackson2.*;
import static com.taveloper.http.test.App.json;
import com.taveloper.http.test.pojo.Activity;
import com.taveloper.http.test.pojo.ActivityFeed;
import com.taveloper.http.test.pojo.parse.ActivityFeedParse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author ZhangZhenli <Taveloper@gmail.com>
 */
public class JsonParserDataBindTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnsupportedEncodingException, IOException {
//        ByteArrayInputStream bais1 = new ByteArrayInputStream(json.getBytes("UTF-8"));
        JacksonFactory factory = new JacksonFactory();
//        ActivityFeed parseAuto = parseAuto(factory, bais1);
        for (int i = 0; i < 10000; i++) {
            ByteArrayInputStream bais = new ByteArrayInputStream(json.getBytes("UTF-8"));
            ActivityFeed parseManual = parseAuto(factory, bais);
//            if (!parseManual.equals(parseAuto)) {
//                System.out.println("No activities found.");
//            }
            System.out.println(i);
        }
    }

    private static ActivityFeed parseAuto(JsonFactory factory, InputStream in) throws IOException {

        JsonParser parser = factory.createJsonParser(in);
        ActivityFeed feed = parser.parseAndClose(ActivityFeed.class, null);
        if (feed.getActivities().isEmpty()) {
            System.out.println("No activities found.");
        } else {
//            for (Activity activity : feed.getActivities()) {
//                System.out.println();
//                System.out.println("-----------------------------------------------");
//                System.out.println("HTML Content: " + activity.getActivityObject().getContent());
//                System.out.println("+1's: " + activity.getActivityObject().getPlusOners().getTotalItems());
//                System.out.println("URL: " + activity.getUrl());
//                System.out.println("ID: " + activity.get("id"));
//            }
        }
        return feed;
    }

    private static ActivityFeed parseManual(com.fasterxml.jackson.core.JsonFactory factory, ByteArrayInputStream bais) throws IOException {
        com.fasterxml.jackson.core.JsonParser parser = factory.createJsonParser(bais);
        JsonParseManual parseManual = new JsonParseManual(parser);
        return parseManual.parseManual(bais, new ActivityFeedParse());
    }
}
