package com.taveloper.http.test;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;
import java.io.IOException;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App {
    
     public static String json = "{\"items\":[{\"id\":\"z13lwnljpxjgt5wn222hcvzimtebslkul\",\"url\":\"https://plus.google.com/116899029375914044550/posts/HYNhBAMeA7U\",\"object\":{\"content\":\"\\u003cb\\u003eWhowilltakethetitleof2011AngryBirdsCollegeChamp?\\u003c/b\\u003e\\u003cbr/\\u003e\\u003cbr/\\u003e\\u003cbr/\\u003eIt&#39;sthe2ndanniversaryofAngryBirdsthisSunday,December11,andtocelebratethisbreak-outgamewe&#39;rehavinganintercollegiateangrybirdschallengeforstudentstocompeteforthetitleof2011AngryBirdsCollegeChampion.Add\\u003cspanclass=\\\"proflinkWrapper\\\"\\u003e\\u003cspanclass=\\\"proflinkPrefix\\\"\\u003e+\\u003c/span\\u003e\\u003cahref=\\\"https://plus.google.com/105912662528057048457\\\"class=\\\"proflink\\\"oid=\\\"105912662528057048457\\\"\\u003eAngryBirdsCollegeChallenge\\u003c/a\\u003e\\u003c/span\\u003etolearnmore.Goodluck,andhavefun!\",\"plusoners\":{\"totalItems\":27}}},{\"id\":\"z13rtboyqt2sit45o04cdp3jxuf5cz2a3e4\",\"url\":\"https://plus.google.com/116899029375914044550/posts/X8W8m9Hk5rE\",\"object\":{\"content\":\"CNNHeroesshinesaspotlightoneverydaypeoplechangingtheworld.Hearthetoptenheroes&#39;inspiringstoriesbytuningintotheCNNbroadcastof&quot;CNNHeroes:AnAll-StarTribute&quot;onSunday,December11,at8pmET/5pmPTwithhost\\u003cspanclass=\\\"proflinkWrapper\\\"\\u003e\\u003cspanclass=\\\"proflinkPrefix\\\"\\u003e+\\u003c/span\\u003e\\u003cahref=\\\"https://plus.google.com/106168900754103197479\\\"class=\\\"proflink\\\"oid=\\\"106168900754103197479\\\"\\u003eAndersonCooper360\\u003c/a\\u003e\\u003c/span\\u003e,anddonatetotheircausesonlineinafewsimplestepswithGoogleWallet(formerlyknownasGoogleCheckout):\\u003cahref=\\\"http://www.google.com/landing/cnnheroes/2011/\\\"\\u003ehttp://www.google.com/landing/cnnheroes/2011/\\u003c/a\\u003e.\",\"plusoners\":{\"totalItems\":21}}},{\"id\":\"z13wtpwpqvihhzeys04cdp3jxuf5cz2a3e4\",\"url\":\"https://plus.google.com/116899029375914044550/posts/dBnaybdLgzU\",\"object\":{\"content\":\"TodaywehostedoneofourBigTenteventsinTheHague.\\u003cspanclass=\\\"proflinkWrapper\\\"\\u003e\\u003cspanclass=\\\"proflinkPrefix\\\"\\u003e+\\u003c/span\\u003e\\u003cahref=\\\"https://plus.google.com/104233435224873922474\\\"class=\\\"proflink\\\"oid=\\\"104233435224873922474\\\"\\u003eEricSchmidt\\u003c/a\\u003e\\u003c/span\\u003e,DutchForeignMinisterUriRosenthal,U.S.SecretaryofStateHillaryClintonandmanyotherscametogethertodiscussfreeexpressionandtheInternet.TheHagueisourthirdBigTent,aplacewherewebringtogethervariousviewpointstodiscussessentialtopicstothefutureoftheInternet.ReadmoreontheOfficialGoogleBloghere:\\u003cahref=\\\"http://goo.gl/d9cSe\\\"\\u003ehttp://goo.gl/d9cSe\\u003c/a\\u003e,andwatchthevideobelowforhighlightsfromtheday.\",\"plusoners\":{\"totalItems\":76}}}]}";


    static boolean isFinish = false;
    static final Object blockLock = new Object();

    public static void main(String[] args) {
        System.out.println("Hello World!");
        Thread thread = new Thread(new GoogleHttpTest(), "GoogleHttpGsonTest");
        thread.start();
        synchronized (blockLock) {
            while (!isFinish) {
                try {
                    blockLock.wait();
                } catch (InterruptedException e) {
                }
            }
        }

    }

    public static class GoogleHttpTest implements Runnable {

        private JsonFactory mJsonFactory;

        @Override
        public void run() {
//            Logger logger = Logger.getLogger("com.google.api.client");
//            logger.setLevel(Level.ALL);
//            logger.addHandler(new Handler() {
//                @Override
//                public void close() throws SecurityException {
//                }
//
//                @Override
//                public void flush() {
//                }
//
//                @Override
//                public void publish(LogRecord record) {
//                    // default ConsoleHandler will take care of >= INFO
//                    if (record.getLevel().intValue() < Level.INFO.intValue()) {
//                        System.out.println(record.getMessage());
//                    }
//                }
//            });
            System.out.println("Start!!");
            HttpTransport httpTransport = new ApacheHttpTransport();

            HttpRequestFactory httpRequestFactory = httpTransport.createRequestFactory();
            mJsonFactory = new JacksonFactory();
            for (int i = 0; i < 1000; i++) {
                GenericUrl url = new GenericUrl("http://192.168.1.2:8099/TestAgent/rest/Test/get");
                JsonObjectParser parser = new JsonObjectParser(mJsonFactory);
                try {
                    HttpRequest httpRequest = httpRequestFactory.buildRequest(HttpMethods.GET, url, null);
                    httpRequest.setLoggingEnabled(true);
                    httpRequest.setParser(parser);
                    HttpResponse httpResponse = httpRequest.execute();
                    ActivityFeed feed = httpResponse.parseAs(ActivityFeed.class);
                    httpResponse.getHeaders();
                    if (feed.getActivities().isEmpty()) {
                        System.out.println(i + "-");
                    } else {
                        System.out.println(i + "+");
//                        if (feed.getActivities().size() == 3) {
//                            System.out.print("First ");
//                        }
//                        System.out.println(feed.getActivities().size() + " activities found:");
//                        for (Activity activity : feed.getActivities()) {
//                            System.out.println();
//                            System.out.println("-----------------------------------------------");
//                            System.out.println("HTML Content: " + activity.getActivityObject().getContent());
//                            System.out.println("+1's: " + activity.getActivityObject().getPlusOners().getTotalItems());
//                            System.out.println("URL: " + activity.getUrl());
//                            System.out.println("ID: " + activity.get("id"));
//                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Over!!!");
            synchronized (blockLock) {
                isFinish = true;
                blockLock.notifyAll();
            }
        }
    }

    /**
     * Feed of Google+ activities.
     */
    public static class ActivityFeed {

        /**
         * List of Google+ activities.
         */
        @Key("items")
        private List<Activity> activities;

        public List<Activity> getActivities() {
            return activities;
        }
    }

    /**
     * Google+ activity.
     */
    public static class Activity extends GenericJson {

        /**
         * Activity URL.
         */
        @Key
        private String url;

        public String getUrl() {
            return url;
        }
        /**
         * Activity object.
         */
        @Key("object")
        private ActivityObject activityObject;

        public ActivityObject getActivityObject() {
            return activityObject;
        }
    }

    /**
     * Google+ activity object.
     */
    public static class ActivityObject {

        /**
         * HTML-formatted content.
         */
        @Key
        private String content;

        public String getContent() {
            return content;
        }
        /**
         * People who +1'd this activity.
         */
        @Key
        private PlusOners plusoners;

        public PlusOners getPlusOners() {
            return plusoners;
        }
    }

    /**
     * People who +1'd an activity.
     */
    public static class PlusOners {

        /**
         * Total number of people who +1'd this activity.
         */
        @Key
        private long totalItems;

        public long getTotalItems() {
            return totalItems;
        }
    }

    private static void parseResponse(HttpResponse response) throws IOException {
        ActivityFeed feed = response.parseAs(ActivityFeed.class);
        if (feed.getActivities().isEmpty()) {
            System.out.println("No activities found.");
        } else {
            System.out.println("activities found.");
            // if (feed.getActivities().size() == MAX_RESULTS) {
            // System.out.print("First ");
            // }
            // System.out.println(feed.getActivities().size() + " activities found:");
            // for (Activity activity : feed.getActivities()) {
            // System.out.println();
            // System.out.println("-----------------------------------------------");
            // System.out.println("HTML Content: " + activity.getActivityObject().getContent());
            // System.out.println("+1's: " + activity.getActivityObject().getPlusOners().getTotalItems());
            // System.out.println("URL: " + activity.getUrl());
            // System.out.println("ID: " + activity.get("id"));
            // }
        }
    }
}
