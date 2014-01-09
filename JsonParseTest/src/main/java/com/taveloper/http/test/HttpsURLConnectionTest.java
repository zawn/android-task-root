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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import com.google.api.client.util.IOUtils;

/**
 *
 * @author ZhangZhenli <zhangzhenli@live.com>
 */
public class HttpsURLConnectionTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
            throws MalformedURLException, IOException, NoSuchAlgorithmException, KeyManagementException {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, null, null);
        URL url = new URL("https://open.t.qq.com/api/user/info?format=json&oauth_consumer_key=801058005&access_token=SplxlOBeZQQYbYS6WxSbIA &openid=null&oauth_version=2.a");
//        URL url = new URL("https://open.t.qq.com/api/user/info?format=json&oauth_consumer_key=801336424&access_token=fde1bb78dbc270e3e05b065bb6c4d4da&openid=703219695EF721146091513317FACC05&clientip=222.95.163.223&oauth_version=2.a");
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setSSLSocketFactory(context.getSocketFactory());
        InputStream in = urlConnection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String readLine = reader.readLine();
        while (readLine != null) {
            System.out.println(readLine);
            readLine = reader.readLine();

        }
    }
}
