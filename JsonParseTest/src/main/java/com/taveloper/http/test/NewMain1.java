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

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import com.taveloper.http.test.pojo.Activity;

/**
 *
 * @author ZhangZhenli <zhangzhenli@live.com>
 */
public class NewMain1 extends SimpleBenchmark {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    Runner.main(NewMain1.class, args);
  }

  /**
   * Benchmark to measure Gson performance for deserializing an object
   */
  public void timeReflectConstructor(int reps) {
    for (int i = 0; i < reps; ++i) {
      try {
        Activity newInstance = Activity.class.newInstance();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  /**
   * Benchmark to measure Gson performance for deserializing an object
   */
  public void timeConstructor(int reps) {
    for (int i = 0; i < reps; ++i) {
      try {
        Activity activity = new Activity();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }
}
