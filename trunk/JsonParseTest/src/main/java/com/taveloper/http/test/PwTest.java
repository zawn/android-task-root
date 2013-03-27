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

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author ZhangZhenli <zhangzhenli@live.com>
 */
public class PwTest {

  private static HttpClient httpClient = new DefaultHttpClient();
  private static String[] a = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "I", "S", "T", "U", "V", "W", "X", "Y", "Z"};

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) throws IOException, InterruptedException {
    ExecutorService es = Executors.newFixedThreadPool(1);
    // 创建一个执行任务的服务     
    try {
      for (int i = 0; i < a.length; i++) {
        String s1 = a[i];
        for (int j = 0; j < a.length; j++) {
          String s2 = a[j];
          Pw pw = new Pw(s1 + s2);
          es.submit(pw);
        }
      }
    } catch (Exception e) {
      System.out.println(e.toString());
    }
    if (es.isShutdown()) {
    } else {
      Thread.sleep(1000);
    }
  }

  private static String convertStreamToString(InputStream is) {
    return new Scanner(is).useDelimiter("\\A").next();
  }

  public static class Pw implements Runnable {

    private final String name;

    public Pw(String name) {
      this.name = name;
    }

    public String pw(String name) throws IOException {
      HttpPost httpPost = new HttpPost("http://registry.pw/domainchecker/process.php?domain=" + name);
      httpPost.setHeader("Host", "registry.pw");
      httpPost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
      httpPost.setHeader("Cookie", " the_cookie=the_value; __utma=146163255.1720719744.1364351585.1364351585.1364351585.1; __utmb=146163255.5.10.1364351585; __utmc=146163255; __utmz=146163255.1364351585.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)");
      httpPost.setHeader("DNT", "1");
      httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:19.0) Gecko/20100101 Firefox/19.0");
      HttpResponse execute = httpClient.execute(httpPost);
      String convertStreamToString = convertStreamToString(execute.getEntity().getContent());
      boolean contains = convertStreamToString.contains("is already taken, try another name.");
      String r;
      if (!contains) {
        r = name + " | " + convertStreamToString;
        System.out.print(name);
        System.out.println(" | " + convertStreamToString);
      } else {
        System.out.println("                X | " + name);
        r = "                              X | " + name;
      }
      return r;
    }

    public String call() throws Exception {
      return pw(name);
    }

    public void run() {
      try {
        pw(name);
      } catch (IOException ex) {
        Logger.getLogger(PwTest.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }
}
