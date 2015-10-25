/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Siemens AG and the thingweb community
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package de.webthing.util.encoding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.webthing.launcher.Launcher;
import de.webthing.servient.impl.ValueType;
import de.webthing.thing.Content;
import de.webthing.thing.MediaType;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Johannes on 20.10.2015.
 */
public class ContentHelper {

    public static final ObjectMapper mapper = new ObjectMapper();
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ContentHelper.class);

    public static Object parse(Content c, Class<?> expected) {
        try {
            switch (c.getMediaType()) {
                case TEXT_PLAIN:
                    return new String(c.getContent());
                case APPLICATION_JSON:
                    return parseJSON(c.getContent(), expected);
                case APPLICATION_XML:
                case APPLICATION_EXI:
                case UNDEFINED:
                default:
                    throw new IllegalArgumentException("406 Not-Acceptable");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("parsing failed",e);
        }
    }

    public static Object parseJSON(byte[] json, Class expected) throws IOException {
        return mapper.readValue(json,expected);
    }

    public static Object parseJSON(String json, Class expected) throws IOException {
        return mapper.readValue(json,expected);
    }

    public static JsonNode readJSON(byte[] json) throws IOException {
        return mapper.readTree(json);
    }

    public static JsonNode readJSON(String json) throws IOException {
        return mapper.readTree(json);
    }

    public static Content wrap(Object content, MediaType type) {
        byte[] res = null;
        switch (type) {
            case APPLICATION_JSON:
                res = wrapJson(content).getBytes();
                break;
            case APPLICATION_XML:
            case APPLICATION_EXI:
            case UNDEFINED:
            case TEXT_PLAIN:
                res =  content.toString().getBytes();
        }
        return new Content(res,type);
    }

    public static String wrapJson(Object content) {
        String json;
        try {
            json = mapper.writer().writeValueAsString(content);
        } catch (JsonProcessingException e) {
            json = "{ \"error\" : \" " + e.getMessage() + "\" , \"input\" : \"" + content.toString() + "\" }";
        }
        return json;
    }

    public static Object getValueFromJson(Content data) {
        Map map = (Map) parse(data, Map.class);
        return map.get("value");
    }

    public static Content makeJsonValue(Object data) {
        return wrap(new ValueType(data),MediaType.APPLICATION_JSON);
    }

    public static <T> T ensureClass(Object o, Class<T> clazz) {
        try {
            return clazz.cast(o);
        } catch(ClassCastException e) {
            String msg = String.format(
                    "expected value to be of type %s, not %s in %s",
                    clazz,
                    o.getClass(),
                    o.toString()
                    );
            log.warn(msg);
            throw new IllegalArgumentException(msg);
        }
    }
}