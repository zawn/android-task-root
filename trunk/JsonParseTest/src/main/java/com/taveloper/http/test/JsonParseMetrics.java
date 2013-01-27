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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.support.json.JsonReader;
import android.support.json.JsonToken;

import com.fasterxml.jackson.core.JsonFactory;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import com.taveloper.http.test.pojo.ActivityFeed;
import com.taveloper.http.test.pojo.gparse.ActivityFeedParse;

/**
 *
 * @author ZhangZhenli <zhangzhenli@live.com>
 */
public class JsonParseMetrics extends SimpleBenchmark {

    public static final String json = "{\"items\":[{\"id\":\"z13lwnljpxjgt5wn222hcvzimtebslkul\",\"url\":\"https://plus.google.com/116899029375914044550/posts/HYNhBAMeA7U\",\"object\":{\"content\":\"\\u003cb\\u003eWhowilltakethetitleof2011AngryBirdsCollegeChamp?\\u003c/b\\u003e\\u003cbr/\\u003e\\u003cbr/\\u003e\\u003cbr/\\u003eIt&#39;sthe2ndanniversaryofAngryBirdsthisSunday,December11,andtocelebratethisbreak-outgamewe&#39;rehavinganintercollegiateangrybirdschallengeforstudentstocompeteforthetitleof2011AngryBirdsCollegeChampion.Add\\u003cspanclass=\\\"proflinkWrapper\\\"\\u003e\\u003cspanclass=\\\"proflinkPrefix\\\"\\u003e+\\u003c/span\\u003e\\u003cahref=\\\"https://plus.google.com/105912662528057048457\\\"class=\\\"proflink\\\"oid=\\\"105912662528057048457\\\"\\u003eAngryBirdsCollegeChallenge\\u003c/a\\u003e\\u003c/span\\u003etolearnmore.Goodluck,andhavefun!\",\"plusoners\":{\"totalItems\":27}}},{\"id\":\"z13rtboyqt2sit45o04cdp3jxuf5cz2a3e4\",\"url\":\"https://plus.google.com/116899029375914044550/posts/X8W8m9Hk5rE\",\"object\":{\"content\":\"CNNHeroesshinesaspotlightoneverydaypeoplechangingtheworld.Hearthetoptenheroes&#39;inspiringstoriesbytuningintotheCNNbroadcastof&quot;CNNHeroes:AnAll-StarTribute&quot;onSunday,December11,at8pmET/5pmPTwithhost\\u003cspanclass=\\\"proflinkWrapper\\\"\\u003e\\u003cspanclass=\\\"proflinkPrefix\\\"\\u003e+\\u003c/span\\u003e\\u003cahref=\\\"https://plus.google.com/106168900754103197479\\\"class=\\\"proflink\\\"oid=\\\"106168900754103197479\\\"\\u003eAndersonCooper360\\u003c/a\\u003e\\u003c/span\\u003e,anddonatetotheircausesonlineinafewsimplestepswithGoogleWallet(formerlyknownasGoogleCheckout):\\u003cahref=\\\"http://www.google.com/landing/cnnheroes/2011/\\\"\\u003ehttp://www.google.com/landing/cnnheroes/2011/\\u003c/a\\u003e.\",\"plusoners\":{\"totalItems\":21}}},{\"id\":\"z13wtpwpqvihhzeys04cdp3jxuf5cz2a3e4\",\"url\":\"https://plus.google.com/116899029375914044550/posts/dBnaybdLgzU\",\"object\":{\"content\":\"TodaywehostedoneofourBigTenteventsinTheHague.\\u003cspanclass=\\\"proflinkWrapper\\\"\\u003e\\u003cspanclass=\\\"proflinkPrefix\\\"\\u003e+\\u003c/span\\u003e\\u003cahref=\\\"https://plus.google.com/104233435224873922474\\\"class=\\\"proflink\\\"oid=\\\"104233435224873922474\\\"\\u003eEricSchmidt\\u003c/a\\u003e\\u003c/span\\u003e,DutchForeignMinisterUriRosenthal,U.S.SecretaryofStateHillaryClintonandmanyotherscametogethertodiscussfreeexpressionandtheInternet.TheHagueisourthirdBigTent,aplacewherewebringtogethervariousviewpointstodiscussessentialtopicstothefutureoftheInternet.ReadmoreontheOfficialGoogleBloghere:\\u003cahref=\\\"http://goo.gl/d9cSe\\\"\\u003ehttp://goo.gl/d9cSe\\u003c/a\\u003e,andwatchthevideobelowforhighlightsfromtheday.\",\"plusoners\":{\"totalItems\":76}}}]}";

    public static void main(String[] args) {
        Runner.main(JsonParseMetrics.class, args);
    }
    private Reader reader;
    private InputStream inputStream;
    private JsonFactory factory;
    private JacksonFactory jacksonFactory;

    @Override
    protected void setUp() throws Exception {
    }

    /**
     * Benchmark to measure Gson performance for deserializing an object
     */
    public void timeGsonStreamParser(int reps) {
        for (int i = 0; i < reps; ++i) {
            try {
                reader = new StringReader(json);
                ActivityFeed parseResult = parseManual(reader);
            } catch (IOException ex) {
                Logger.getLogger(JsonParseMetrics.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static ActivityFeed parseManual(Reader reader) throws IOException {
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

    /**
     * Benchmark to measure deserializing objects by hand
     */
    public void timeJacksonStreamParser(int reps) throws IOException {
        for (int i = 0; i < reps; ++i) {
            factory = new com.fasterxml.jackson.core.JsonFactory();
            inputStream = new ByteArrayInputStream(json.getBytes("UTF-8"));
            ActivityFeed parseManual = parseManual(factory, inputStream);
        }
    }

    private static ActivityFeed parseManual(com.fasterxml.jackson.core.JsonFactory factory, InputStream bais) throws IOException {
        com.fasterxml.jackson.core.JsonParser parser = factory.createJsonParser(bais);
        JsonParseManual parseManual = new JsonParseManual(parser);
        return parseManual.parseManual(bais, new com.taveloper.http.test.pojo.parse.ActivityFeedParse());
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
        JsonParser parser = jacksonFactory.createJsonParser(in);
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
}
