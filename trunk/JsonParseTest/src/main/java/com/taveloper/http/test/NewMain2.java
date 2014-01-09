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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author ZhangZhenli <zhangzhenli@live.com>
 */
public class NewMain2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, FileNotFoundException, IOException {
        //    Method method = NewMain1.class.getMethod("main");
        //    Object invoke = method.invoke(null);
//        Properties properties = System.getProperties();
//        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
//            Object key = entry.getKey();
//            Object value = entry.getValue();
//            System.out.println(key+"="+value+",");
//
//        }
        // eg:callback( {"client_id":"100422774","openid":"C48C8E8B67B9DC2BB2B9879486BD13BA"} );
        // eg:callback( {"error":100016,"error_description":"access token check failed"} );
        FileReader fileReader = new FileReader("G:\\a.txt");
        BufferedReader br = new BufferedReader(fileReader);
        String readLine = br.readLine();
        int begin = readLine.indexOf("(");
        int end = readLine.indexOf(")");
        String substring = readLine.substring(begin+1, end-1);
        System.out.println(substring);

    }
}
