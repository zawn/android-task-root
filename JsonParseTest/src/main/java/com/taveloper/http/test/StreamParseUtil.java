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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.common.base.Preconditions;
import com.google.gson.stream.JsonReader;
import java.io.IOException;

/**
 *
 * @author ZhangZhenli <zhangzhenli@live.com>
 */
public class StreamParseUtil {

    /**
     * Starts parsing an object or array by making sure the parser points to an
     * object field name, first array value or end of object or array. <p> If
     * the parser is at the start of input, {@link #nextToken()} is called. The
     * current token must then be {@link JsonToken#START_OBJECT}, {@link JsonToken#END_OBJECT},
     * {@link JsonToken#START_ARRAY}, {@link JsonToken#END_ARRAY}, or
     * {@link JsonToken#FIELD_NAME}. For an object only, after the method is
     * called, the current token must be either {@link JsonToken#FIELD_NAME} or
     * {@link JsonToken#END_OBJECT}. </p>
     */
    public static JsonToken startParsingObjectOrArray(JsonParser parser) throws IOException {
        JsonToken currentToken = startParsing(parser);
        switch (currentToken) {
            case START_OBJECT:
                currentToken = nextToken(parser);
                Preconditions.checkArgument(
                        currentToken == JsonToken.FIELD_NAME || currentToken == JsonToken.END_OBJECT,
                        currentToken);
                break;
            case START_ARRAY:
                currentToken = nextToken(parser);
                break;
        }
        return currentToken;
    }

    /**
     * Starts parsing an object or array by making sure the parser points to an
     * object field name, first array value or end of object or array. <p> If
     * the parser is at the start of input, {@link #nextToken()} is called. The
     * current token must then be {@link JsonToken#START_OBJECT}, {@link JsonToken#END_OBJECT},
     * {@link JsonToken#START_ARRAY}, {@link JsonToken#END_ARRAY}, or
     * {@link JsonToken#FIELD_NAME}. For an object only, after the method is
     * called, the current token must be either {@link JsonToken#FIELD_NAME} or
     * {@link JsonToken#END_OBJECT}. </p>
     */
    public static JsonToken startParsingObject(JsonParser parser) throws IOException {
        JsonToken currentToken = startParsing(parser);
        if (currentToken != JsonToken.START_OBJECT) {
            currentToken = nextToken(parser);
        }
//        Preconditions.checkArgument(currentToken == JsonToken.START_OBJECT, currentToken + "is not a Json Object!");
        currentToken = nextToken(parser);
//        Preconditions.checkArgument(
//                currentToken == JsonToken.FIELD_NAME || currentToken == JsonToken.END_OBJECT,
//                currentToken);
        return currentToken;
    }

    /**
     * Starts parsing that handles start of input by calling
     * {@link #nextToken()}.
     */
    public static JsonToken startParsing(JsonParser parser) throws IOException {
        JsonToken currentToken = getCurrentToken(parser);
        // token is null at start, so get next token
        if (currentToken == null) {
            currentToken = nextToken(parser);
        }
        Preconditions.checkArgument(currentToken != null, "no JSON input found");
        return currentToken;
    }

    public static JsonToken getCurrentToken(JsonParser parser) {
        return parser.getCurrentToken();
    }

    public static JsonToken nextToken(JsonParser parser) throws IOException {
        return parser.nextToken();
    }
}
