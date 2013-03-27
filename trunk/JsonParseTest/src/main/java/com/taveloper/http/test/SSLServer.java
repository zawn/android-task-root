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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;

/**
 *
 * @author ZhangZhenli <zhangzhenli@live.com>
 */
public class SSLServer extends Thread {

  private Socket socket;

  public SSLServer(Socket socket) {
    this.socket = socket;
  }

  public void run() {
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter writer = new PrintWriter(socket.getOutputStream());

      String data = reader.readLine();
//HTTP/1.1 200 OK
//Content-Type: text/plain; charset=UTF-8
//Content-Length: 983
//Date: Mon, 28 Jan 2013 03:09:26 GMT
//Server: GFE/2.0

      writer.println("HTTP/1.1 200 OK");
      writer.println("Content-Type: text/plain; charset=UTF-8");
      writer.println("Content-Length: "+data.length());
      writer.println(data);
      writer.println();
      writer.close();
      socket.close();
    } catch (IOException e) {
    }
  }
  private static String SERVER_KEY_STORE = "C:\\Users\\Yutian\\server_ks";
  private static String SERVER_KEY_STORE_PASSWORD = "123123";

  public static void main(String[] args) throws Exception {
    System.setProperty("javax.net.ssl.trustStore", SERVER_KEY_STORE);
    SSLContext context = SSLContext.getInstance("TLS");

    KeyStore ks = KeyStore.getInstance("jceks");
    ks.load(new FileInputStream(SERVER_KEY_STORE), null);
    KeyManagerFactory kf = KeyManagerFactory.getInstance("SunX509");
    kf.init(ks, SERVER_KEY_STORE_PASSWORD.toCharArray());

    context.init(kf.getKeyManagers(), null, null);

    ServerSocketFactory factory = context.getServerSocketFactory();
    ServerSocket _socket = factory.createServerSocket(8444);
    ((SSLServerSocket) _socket).setNeedClientAuth(false);

    while (true) {
      new SSLServer(_socket.accept()).start();
    }
  }
}
