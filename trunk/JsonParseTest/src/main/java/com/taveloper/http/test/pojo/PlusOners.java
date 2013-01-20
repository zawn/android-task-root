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
public class PlusOners {

    /**
     * Total number of people who +1'd this activity.
     */
    @Key
    private long totalItems;

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    public long getTotalItems() {
        return totalItems;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (int) (this.totalItems ^ (this.totalItems >>> 32));
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
        final PlusOners other = (PlusOners) obj;
        if (this.totalItems != other.totalItems) {
            return false;
        }
        return true;
    }
}