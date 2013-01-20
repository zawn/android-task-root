/*
 * Copyright 2013 ZhangZhenli <Taveloper@gmail.com>.
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
package com.taveloper.http.test.pojo;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

/**
 *
 * @author ZhangZhenli <Taveloper@gmail.com>
 */
public class Activity extends GenericJson {

    /**
     * Activity URL.
     */
    @Key
    private String url;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setActivityObject(ActivityObject activityObject) {
        this.activityObject = activityObject;
    }

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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.url != null ? this.url.hashCode() : 0);
        hash = 79 * hash + (this.activityObject != null ? this.activityObject.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Activity other = (Activity) obj;
        if ((this.url == null) ? (other.url != null) : !this.url.equals(other.url)) {
            return false;
        }
        if (this.activityObject != other.activityObject && (this.activityObject == null || !this.activityObject.equals(other.activityObject))) {
            return false;
        }
        return true;
    }
}
