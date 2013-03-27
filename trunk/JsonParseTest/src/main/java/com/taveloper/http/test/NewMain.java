package com.taveloper.http.test;

import java.io.FileInputStream;
import java.security.KeyStore;

import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport.Builder;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.SslUtils;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Hello world!
 *
 */
public class NewMain {

  static boolean isFinish = false;
  static final Object blockLock = new Object();
  private static String CLIENT_KEY_STORE = "C:\\Users\\Yutian\\client_ks";
  private static String CLIENT_KEY_STORE_PASSWORD = "456456";

  public static void main(String[] args) {
    try {
      System.out.println("Hello World!");
      System.out.println("Start!!");
      System.setProperty("javax.net.debug", "ssl,handshake");
      FileInputStream fileInputStream = new FileInputStream(CLIENT_KEY_STORE);
      KeyStore keyStore = KeyStore.getInstance("JKS");
      keyStore.load(fileInputStream, "client".toCharArray());

      SSLContext context = SSLContext.getInstance("TLS");
      KeyStore ks = KeyStore.getInstance("jceks");

      ks.load(new FileInputStream(CLIENT_KEY_STORE), null);
      KeyManagerFactory kf = KeyManagerFactory.getInstance("SunX509");
      TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
      kf.init(ks, CLIENT_KEY_STORE_PASSWORD.toCharArray());
      tmf.init(ks);
      context.init(kf.getKeyManagers(), tmf.getTrustManagers(), null);
      SSLSocketFactory socketFactory = context.getSocketFactory();


      Builder builder = new NetHttpTransport.Builder();
      builder.setSslSocketFactory(socketFactory);
//      builder.trustCertificates(keyStore);
      builder.setHostnameVerifier(SslUtils.trustAllHostnameVerifier());

      HttpRequestFactory httpRequestFactory = builder.build().createRequestFactory();
      JacksonFactory mJsonFactory = new JacksonFactory();
      for (int i = 0; i < 1; i++) {
        JsonObjectParser parser = new JsonObjectParser(mJsonFactory);
        String url = "https://192.168.1.2:8444/";
        HttpRequest httpRequest = httpRequestFactory.buildRequest(HttpMethods.GET, url, null);
        httpRequest.setLoggingEnabled(true);
        httpRequest.setParser(parser);
        HttpResponse httpResponse = httpRequest.execute();
        App.ActivityFeed feed = httpResponse.parseAs(App.ActivityFeed.class);
        httpResponse.getHeaders();
        if (feed.getActivities().isEmpty()) {
          System.out.println(i + "-");
        } else {
          System.out.println(i + "+");
          if (feed.getActivities().size() == 3) {
            System.out.print("First ");
          }
          System.out.println(feed.getActivities().size() + " activities found:");
          for (App.Activity activity : feed.getActivities()) {
            System.out.println();
            System.out.println("-----------------------------------------------");
            System.out.println("HTML Content: " + activity.getActivityObject().getContent());
            System.out.println("+1's: " + activity.getActivityObject().getPlusOners().getTotalItems());
            System.out.println("URL: " + activity.getUrl());
            System.out.println("ID: " + activity.get("id"));
          }
        }
      }
      System.out.println("Over!!!");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
