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

package de.webthing.desc;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import de.webthing.desc.pojo.ThingDescription;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class DescriptionParser {

    // default JSON-LD context for TDs
    private static Object context;

    static {
        try {
            context = JsonUtils.fromURL(new URL(
                    "http://w3c.github.io/wot/w3c-wot-td-context.jsonld"));
        } catch (IOException e) {
            e.printStackTrace();
            context = null;
        }
    }

    @SuppressWarnings("unchecked")
    // note: the jsonld-java implementation uses java.util.LinkedHashMap to store JSON objects
    // see http://wiki.fasterxml.com/JacksonInFiveMinutes
    private static JSONObject compactJson(Object jsonld) throws IOException {
        if (context == null) {
            throw new IOException("Default TD context could not be retrieved");
        }

        try {
            jsonld = JsonLdProcessor.compact(jsonld, context, new JsonLdOptions());
            return new JSONObject((Map<String, Object>) jsonld);
        } catch (JsonLdError e) {
            throw new IOException("The input object is not valid JSON-LD", e);
        }
    }

    private static ThingDescription mapJson(Object jsonld) throws IOException {
        // ensures keys are reduced to those in the default context
        JSONObject json = compactJson(jsonld);

        ObjectMapper mapper = new ObjectMapper();
        ThingDescription td = mapper.readValue(json.toString(), ThingDescription.class);
        return td;
    }

    public static ThingDescription fromURL(URL url) throws JsonParseException,
            IOException {
        Object jsonld = JsonUtils.fromURL(url);
        return mapJson(jsonld);
    }

    public static ThingDescription fromFile(String fname)
            throws FileNotFoundException, IOException {
        Object jsonld = JsonUtils.fromReader(new FileReader(fname));
        return mapJson(jsonld);
    }

}
