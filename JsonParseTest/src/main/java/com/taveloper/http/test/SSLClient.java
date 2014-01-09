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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.security.KeyStore;

import javax.net.SocketFactory;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 *
 * @author ZhangZhenli <zhangzhenli@live.com>
 */
public class SSLClient {

  private static String CLIENT_KEY_STORE = "C:\\Users\\Yutian\\client_ks";

  public static void main(String[] args) throws Exception {
    // Set the key store to use for validating the server cert.
//    System.setProperty("javax.net.ssl.trustStore", CLIENT_KEY_STORE);

    System.setProperty("javax.net.debug", "ssl,handshake");

    SSLClient client = new SSLClient();
    Socket s = client.clientWithoutCert();

    PrintWriter writer = new PrintWriter(s.getOutputStream());
    BufferedReader reader = new BufferedReader(new InputStreamReader(s
            .getInputStream()));
    writer.println("hello");
    writer.flush();
    System.out.println(reader.readLine());
    s.close();
  }

  private Socket clientWithoutCert() throws Exception {
    FileInputStream fileInputStream = new FileInputStream(CLIENT_KEY_STORE);
    KeyStore keyStore = KeyStore.getInstance("JKS");
    keyStore.load(fileInputStream, "client".toCharArray());
    TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
    tmf.init(keyStore);

    SSLContext context = SSLContext.getInstance("TLS");
    context.init(null, tmf.getTrustManagers(), null);

    URL url = new URL("https://www.example.com/");
    HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
    urlConnection.setSSLSocketFactory(context.getSocketFactory());
    InputStream in = urlConnection.getInputStream();


    SocketFactory sf = context.getSocketFactory();
    Socket s = sf.createSocket("localhost", 8444);
    return s;
  }
}
