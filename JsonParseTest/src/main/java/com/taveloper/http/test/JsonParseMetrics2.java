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

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import com.taveloper.http.test.pojo.ActivityFeed;
import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.JsonFactory;

/**
 *
 * @author ZhangZhenli <zhangzhenli@live.com>
 */
public class JsonParseMetrics2 extends SimpleBenchmark {

    public static final String json = "{\"items\":[{\"id\":\"z13lwnljpxjgt5wn222hcvzimtebslkul\",\"url\":\"https://plus.google.com/116899029375914044550/posts/HYNhBAMeA7U\",\"object\":{\"content\":\"\\u003cb\\u003eWhowilltakethetitleof2011AngryBirdsCollegeChamp?\\u003c/b\\u003e\\u003cbr/\\u003e\\u003cbr/\\u003e\\u003cbr/\\u003eIt&#39;sthe2ndanniversaryofAngryBirdsthisSunday,December11,andtocelebratethisbreak-outgamewe&#39;rehavinganintercollegiateangrybirdschallengeforstudentstocompeteforthetitleof2011AngryBirdsCollegeChampion.Add\\u003cspanclass=\\\"proflinkWrapper\\\"\\u003e\\u003cspanclass=\\\"proflinkPrefix\\\"\\u003e+\\u003c/span\\u003e\\u003cahref=\\\"https://plus.google.com/105912662528057048457\\\"class=\\\"proflink\\\"oid=\\\"105912662528057048457\\\"\\u003eAngryBirdsCollegeChallenge\\u003c/a\\u003e\\u003c/span\\u003etolearnmore.Goodluck,andhavefun!\",\"plusoners\":{\"totalItems\":27}}},{\"id\":\"z13rtboyqt2sit45o04cdp3jxuf5cz2a3e4\",\"url\":\"https://plus.google.com/116899029375914044550/posts/X8W8m9Hk5rE\",\"object\":{\"content\":\"CNNHeroesshinesaspotlightoneverydaypeoplechangingtheworld.Hearthetoptenheroes&#39;inspiringstoriesbytuningintotheCNNbroadcastof&quot;CNNHeroes:AnAll-StarTribute&quot;onSunday,December11,at8pmET/5pmPTwithhost\\u003cspanclass=\\\"proflinkWrapper\\\"\\u003e\\u003cspanclass=\\\"proflinkPrefix\\\"\\u003e+\\u003c/span\\u003e\\u003cahref=\\\"https://plus.google.com/106168900754103197479\\\"class=\\\"proflink\\\"oid=\\\"106168900754103197479\\\"\\u003eAndersonCooper360\\u003c/a\\u003e\\u003c/span\\u003e,anddonatetotheircausesonlineinafewsimplestepswithGoogleWallet(formerlyknownasGoogleCheckout):\\u003cahref=\\\"http://www.google.com/landing/cnnheroes/2011/\\\"\\u003ehttp://www.google.com/landing/cnnheroes/2011/\\u003c/a\\u003e.\",\"plusoners\":{\"totalItems\":21}}},{\"id\":\"z13wtpwpqvihhzeys04cdp3jxuf5cz2a3e4\",\"url\":\"https://plus.google.com/116899029375914044550/posts/dBnaybdLgzU\",\"object\":{\"content\":\"TodaywehostedoneofourBigTenteventsinTheHague.\\u003cspanclass=\\\"proflinkWrapper\\\"\\u003e\\u003cspanclass=\\\"proflinkPrefix\\\"\\u003e+\\u003c/span\\u003e\\u003cahref=\\\"https://plus.google.com/104233435224873922474\\\"class=\\\"proflink\\\"oid=\\\"104233435224873922474\\\"\\u003eEricSchmidt\\u003c/a\\u003e\\u003c/span\\u003e,DutchForeignMinisterUriRosenthal,U.S.SecretaryofStateHillaryClintonandmanyotherscametogethertodiscussfreeexpressionandtheInternet.TheHagueisourthirdBigTent,aplacewherewebringtogethervariousviewpointstodiscussessentialtopicstothefutureoftheInternet.ReadmoreontheOfficialGoogleBloghere:\\u003cahref=\\\"http://goo.gl/d9cSe\\\"\\u003ehttp://goo.gl/d9cSe\\u003c/a\\u003e,andwatchthevideobelowforhighlightsfromtheday.\",\"plusoners\":{\"totalItems\":76}}}]}";

    public static void main(String[] args) {
        Runner.main(JsonParseMetrics2.class, args);
    }
    private Reader reader;
    private InputStream inputStream;
    private JsonFactory factory;
    private JacksonFactory jacksonFactory;
    private char[] data;

    @Override
    protected void setUp() throws Exception {
        data = json.toCharArray();
    }

    /**
     * Benchmark to measure Gson performance for deserializing an object
     */
    public void timeGsonStreamParser(int reps) {
        for (int i = 0; i < reps; ++i) {
            Parser parser = new GsonStreamParser();
            try {
                parser.parse(data);
            } catch (Exception ex) {
                Logger.getLogger(JsonParseMetrics2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

//    /**
//     * Benchmark to measure deserializing objects by hand
//     */
//    public void timeJackson1StreamParser(int reps) throws IOException {
//        for (int i = 0; i < reps; ++i) {
//            Parser parser = new JacksonStreamParser();
//            try {
//                parser.parse(data);
//            } catch (Exception ex) {
//                Logger.getLogger(JsonParseMetrics2.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//    }

    /**
     * Benchmark to measure deserializing objects by hand
     */
    public void timeJackson2StreamParser(int reps) throws IOException {
        for (int i = 0; i < reps; ++i) {
            Parser parser = new Jackson2StreamParser();
            try {
                parser.parse(data);
            } catch (Exception ex) {
                Logger.getLogger(JsonParseMetrics2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Benchmark to measure deserializing objects by hand
     */
    public void timeJacksonDataBind(int reps) throws IOException {
        for (int i = 0; i < reps; ++i) {
            jacksonFactory = new JacksonFactory();
            inputStream = new ByteArrayInputStream(json.getBytes("UTF-8"));
            ActivityFeed dataBind = parseAuto(jacksonFactory, inputStream);
        }
    }

    private ActivityFeed parseAuto(JacksonFactory jacksonFactory, InputStream in) throws IOException {
        com.google.api.client.json.JsonParser parser = jacksonFactory.createJsonParser(in);
        ActivityFeed feed = parser.parseAndClose(ActivityFeed.class, null);
//        if (feed.getActivities().isEmpty()) {
//            System.out.println("No activities found.");
//        } else {
//            for (Activity activity : feed.getActivities()) {
//                System.out.println();
//                System.out.println("-----------------------------------------------");
//                System.out.println("HTML Content: " + activity.getActivityObject().getContent());
//                System.out.println("+1's: " + activity.getActivityObject().getPlusOners().getTotalItems());
//                System.out.println("URL: " + activity.getUrl());
//                System.out.println("ID: " + activity.get("id"));
//            }
//        }
        return feed;
    }

    interface Parser {

        void parse(char[] data) throws Exception;
    }

    private static class GsonStreamParser implements Parser {

        public void parse(char[] data) throws Exception {
            com.google.gson.stream.JsonReader jsonReader = new com.google.gson.stream.JsonReader(new CharArrayReader(data));
            readToken(jsonReader);
            jsonReader.close();
        }

        private void readToken(com.google.gson.stream.JsonReader reader) throws IOException {
            while (true) {
                switch (reader.peek()) {
                    case BEGIN_ARRAY:
                        reader.beginArray();
                        break;
                    case END_ARRAY:
                        reader.endArray();
                        break;
                    case BEGIN_OBJECT:
                        reader.beginObject();
                        break;
                    case END_OBJECT:
                        reader.endObject();
                        break;
                    case NAME:
                        reader.nextName();
                        break;
                    case BOOLEAN:
                        reader.nextBoolean();
                        break;
                    case NULL:
                        reader.nextNull();
                        break;
                    case NUMBER:
                        reader.nextLong();
                        break;
                    case STRING:
                        reader.nextString();
                        break;
                    case END_DOCUMENT:
                        return;
                    default:
                        throw new IllegalArgumentException("Unexpected token" + reader.peek());
                }
            }
        }
    }
//
//    private static class JacksonStreamParser implements Parser {
//
//        public void parse(char[] data) throws Exception {
//            JsonFactory jsonFactory = new JsonFactory();
//            org.codehaus.jackson.JsonParser jp = jsonFactory.createJsonParser(new CharArrayReader(data));
//            jp.configure(org.codehaus.jackson.JsonParser.Feature.CANONICALIZE_FIELD_NAMES, false);
//            int depth = 0;
//            do {
//                switch (jp.nextToken()) {
//                    case START_OBJECT:
//                    case START_ARRAY:
//                        depth++;
//                        break;
//                    case END_OBJECT:
//                    case END_ARRAY:
//                        depth--;
//                        break;
//                    case FIELD_NAME:
//                        jp.getCurrentName();
//                        break;
//                    case VALUE_STRING:
//                        jp.getText();
//                        break;
//                    case VALUE_NUMBER_INT:
//                    case VALUE_NUMBER_FLOAT:
//                        jp.getLongValue();
//                        break;
//                }
//            } while (depth > 0);
//            jp.close();
//        }
//    }

    private static class Jackson2StreamParser implements Parser {

        public void parse(char[] data) throws Exception {
            com.fasterxml.jackson.core.JsonFactory jsonFactory = new com.fasterxml.jackson.core.JsonFactory();
            com.fasterxml.jackson.core.JsonParser jp = jsonFactory.createJsonParser(new CharArrayReader(data));
//            jp.configure(org.codehaus.jackson.JsonParser.Feature.CANONICALIZE_FIELD_NAMES, false);
            int depth = 0;
            do {
                switch (jp.nextToken()) {
                    case START_OBJECT:
                    case START_ARRAY:
                        depth++;
                        break;
                    case END_OBJECT:
                    case END_ARRAY:
                        depth--;
                        break;
                    case FIELD_NAME:
                        jp.getCurrentName();
                        break;
                    case VALUE_STRING:
                        jp.getText();
                        break;
                    case VALUE_NUMBER_INT:
                    case VALUE_NUMBER_FLOAT:
                        jp.getLongValue();
                        break;
                }
            } while (depth > 0);
            jp.close();
        }
    }
}
