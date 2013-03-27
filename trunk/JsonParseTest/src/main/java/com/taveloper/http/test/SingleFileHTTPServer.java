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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;

/**
 *
 * @author ZhangZhenli <zhangzhenli@live.com>
 */
public class SingleFileHTTPServer extends Thread {

  private byte[] content;
  private byte[] header;
  private int port = 80;
  private static String SERVER_KEY_STORE = "C:\\Users\\Yutian\\server_ks";
  private static String SERVER_KEY_STORE_PASSWORD = "123123";
  public static String json = "{\"items\":[{\"id\":\"z13lwnljpxjgt5wn222hcvzimtebs"
          + "lkul\",\"url\":\"https://plus.google.com/116899029375914044550/posts/H"
          + "YNhBAMeA7U\",\"object\":{\"content\":\"\\u003cb\\u003eWhowilltakethetit"
          + "leof2011AngryBirdsCollegeChamp?\\u003c/b\\u003e\\u003cbr/\\u003e\\u003"
          + "cbr/\\u003e\\u003cbr/\\u003eIt&#39;sthe2ndanniversaryofAngryBirdsthisSu"
          + "nday,December11,andtocelebratethisbreak-outgamewe&#39;rehavinganinterco"
          + "llegiateangrybirdschallengeforstudentstocompeteforthetitleof2011AngryBi"
          + "rdsCollegeChampion.Add\\u003cspanclass=\\\"proflinkWrapper\\\"\\u003e\\"
          + "u003cspanclass=\\\"proflinkPrefix\\\"\\u003e+\\u003c/span\\u003e\\u003ca"
          + "href=\\\"https://plus.google.com/105912662528057048457\\\"class=\\\"prof"
          + "link\\\"oid=\\\"105912662528057048457\\\"\\u003eAngryBirdsCollegeChallen"
          + "ge\\u003c/a\\u003e\\u003c/span\\u003etolearnmore.Goodluck,andhavefun!\","
          + "\"plusoners\":{\"totalItems\":27}}},{\"id\":\"z13rtboyqt2sit45o04cdp3jxu"
          + "f5cz2a3e4\",\"url\":\"https://plus.google.com/116899029375914044550/post"
          + "s/X8W8m9Hk5rE\",\"object\":{\"content\":\"CNNHeroesshinesaspotlightoneve"
          + "rydaypeoplechangingtheworld.Hearthetoptenheroes&#39;inspiringstoriesbytu"
          + "ningintotheCNNbroadcastof&quot;CNNHeroes:AnAll-StarTribute&quot;onSunday"
          + ",December11,at8pmET/5pmPTwithhost\\u003cspanclass=\\\"proflinkWrapper\\\"\\u0"
          + "03e\\u003cspanclass=\\\"proflinkPrefix\\\"\\u003e+\\u003c/span\\u003e\\u003c"
          + "ahref=\\\"https://plus.google.com/106168900754103197479\\\"class=\\\"profl"
          + "ink\\\"oid=\\\"106168900754103197479\\\"\\u003eAndersonCooper360\\u003c/"
          + "a\\u003e\\u003c/span\\u003e,anddonatetotheircausesonlineinafewsimplestep"
          + "swithGoogleWallet(formerlyknownasGoogleCheckout):\\u003cahref=\\\"http://ww"
          + "w.google.com/landing/cnnheroes/2011/\\\"\\u003ehttp://www.google.com/landi"
          + "ng/cnnheroes/2011/\\u003c/a\\u003e.\",\"plusoners\":{\"totalItems\":21}}"
          + "},{\"id\":\"z13wtpwpqvihhzeys04cdp3jxuf5cz2a3e4\",\"url\":\"https://plus"
          + ".google.com/116899029375914044550/posts/dBnaybdLgzU\",\"object\":{\"cont"
          + "ent\":\"TodaywehostedoneofourBigTenteventsinTheHague.\\u003cspanclass=\\\"pr"
          + "oflinkWrapper\\\"\\u003e\\u003cspanclass=\\\"proflinkPrefix\\\"\\u003e+\\u00"
          + "3c/span\\u003e\\u003cahref=\\\"https://plus.google.com/10423343522487392247"
          + "4\\\"class=\\\"proflink\\\"oid=\\\"104233435224873922474\\\"\\u003eEricSch"
          + "midt\\u003c/a\\u003e\\u003c/span\\u003e,DutchForeignMinisterUriRosenthal,U"
          + ".S.SecretaryofStateHillaryClintonandmanyotherscametogethertodiscussfreeexp"
          + "ressionandtheInternet.TheHagueisourthirdBigTent,aplacewherewebringtogether"
          + "variousviewpointstodiscussessentialtopicstothefutureoftheInternet.Readmore"
          + "ontheOfficialGoogleBloghere:\\u003cahref=\\\"http://goo.gl/d9cSe\\\"\\u003"
          + "ehttp://goo.gl/d9cSe\\u003c/a\\u003e,andwatchthevideobelowforhighlightsfrom"
          + "theday.\",\"plusoners\":{\"totalItems\":76}}}]}";

  private SingleFileHTTPServer(String data, String encoding,
          String MIMEType, int port) throws UnsupportedEncodingException {
    this(data.getBytes(encoding), encoding, MIMEType, port);
  }

  public SingleFileHTTPServer(byte[] data, String encoding, String MIMEType, int port) throws UnsupportedEncodingException {
    this.content = data;
    this.port = port;
    String header = "HTTP/1.0 200 OK\r\n"
            + "Server: OneFile 1.0\r\n"
            + "Content-length: " + this.content.length + "\r\n"
            + "Content-type: " + MIMEType + "\r\n\r\n";
    this.header = header.getBytes("ASCII");
  }

  public void run() {
    try {
       System.setProperty("javax.net.debug", "ssl,handshake");

      System.setProperty("javax.net.ssl.trustStore", SERVER_KEY_STORE);
      SSLContext context = SSLContext.getInstance("TLS");

      KeyStore ks = KeyStore.getInstance("jceks");
      ks.load(new FileInputStream(SERVER_KEY_STORE), null);
      KeyManagerFactory kf = KeyManagerFactory.getInstance("SunX509");
      kf.init(ks, SERVER_KEY_STORE_PASSWORD.toCharArray());

      context.init(kf.getKeyManagers(), null, null);

      ServerSocketFactory factory = context.getServerSocketFactory();
      ServerSocket server = factory.createServerSocket(this.port);
      ((SSLServerSocket) server).setNeedClientAuth(true);
//      ServerSocket server = new ServerSocket(this.port);
      System.out.println("Accepting connections on port " + server.getLocalPort());
      System.out.println("Data to be sent:");
      System.out.write(this.content);

      while (true) {
        Socket connection = null;
        try {
          connection = server.accept();
          OutputStream out = new BufferedOutputStream(connection.getOutputStream());
          InputStream in = new BufferedInputStream(connection.getInputStream());

          StringBuffer request = new StringBuffer();
          while (true) {
            int c = in.read();
            if (c == '\r' || c == '\n' || c == -1) {
              break;
            }
            request.append((char) c);

          }

          //如果检测到是HTTP/1.0及以后的协议，按照规范，需要发送一个MIME首部
          if (request.toString().indexOf("HTTP/") != -1) {
            out.write(this.header);
          }

          out.write(this.content);
          out.flush();

        } catch (IOException e) {
          // TODO: handle exception
        } finally {
          if (connection != null) {
            connection.close();
          }
        }
      }

    } catch (KeyManagementException ex) {
      Logger.getLogger(SingleFileHTTPServer.class.getName()).log(Level.SEVERE, null, ex);
    } catch (UnrecoverableKeyException ex) {
      Logger.getLogger(SingleFileHTTPServer.class.getName()).log(Level.SEVERE, null, ex);
    } catch (CertificateException ex) {
      Logger.getLogger(SingleFileHTTPServer.class.getName()).log(Level.SEVERE, null, ex);
    } catch (KeyStoreException ex) {
      Logger.getLogger(SingleFileHTTPServer.class.getName()).log(Level.SEVERE, null, ex);
    } catch (NoSuchAlgorithmException ex) {
      Logger.getLogger(SingleFileHTTPServer.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException e) {
      System.err.println("Could not start server. Port Occupied");
    }
  }

  public static void main(String[] args) {
    try {
      byte[] data = json.getBytes("UTF-8");

      Thread t = new SingleFileHTTPServer(data, "UTF-8", "text/plain", 8444);
      t.start();

    } catch (ArrayIndexOutOfBoundsException e) {
      System.out.println("Usage:java SingleFileHTTPServer filename port encoding");
    } catch (Exception e) {
      System.err.println(e);// TODO: handle exception
    }
  }
}
