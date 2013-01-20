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

import com.google.api.client.util.Key;

/**
 *
 * @author ZhangZhenli <Taveloper@gmail.com>
 */
public class ActivityObject {

    /**
     * HTML-formatted content.
     */
    @Key
    private String content;
    /**
     * People who +1'd this activity.
     */
    @Key
    private PlusOners plusoners;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public PlusOners getPlusOners() {
        return plusoners;
    }

    public void setPlusOners(PlusOners plusoners) {
        this.plusoners = plusoners;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.content != null ? this.content.hashCode() : 0);
        hash = 79 * hash + (this.plusoners != null ? this.plusoners.hashCode() : 0);
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
        final ActivityObject other = (ActivityObject) obj;
        if ((this.content == null) ? (other.content != null) : !this.content.equals(other.content)) {
            return false;
        }
        if (this.plusoners != other.plusoners && (this.plusoners == null || !this.plusoners.equals(other.plusoners))) {
            return false;
        }
        return true;
    }
}
