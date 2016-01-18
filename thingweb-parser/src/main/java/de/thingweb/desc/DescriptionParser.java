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

package de.thingweb.desc;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.core.JsonLdUtils;
import com.github.jsonldjava.utils.JsonUtils;
import com.siemens.ct.exi.exceptions.EXIException;

import de.thingweb.desc.pojo.ThingDescription;
import de.thingweb.encoding.json.exi.EXI4JSONParser;

import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
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
    	
    	InputStream is = new BufferedInputStream(url.openStream());
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	int b;
    	while((b = is.read()) != -1) {
    		baos.write(b);
    	}
    	return fromBytes(baos.toByteArray());
    	
//        Object jsonld = JsonUtils.fromURL(url);
//        return mapJson(jsonld);
    }
    
    public static ThingDescription fromBytes(byte[] data) throws JsonParseException, IOException {
    	ByteArrayInputStream bais = new ByteArrayInputStream(data);
    	
    	// check whether we deal with an exified JSON file
    	bais.mark(5); // latest after 4 byte cookie and 2 distinguishing bits it is clear whether we deal with an EXI file
    	try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			EXI4JSONParser e4j = new EXI4JSONParser(new PrintStream(baos));
			e4j.parse(new InputSource(bais));
			
			// adapt to new input stream
			bais = new ByteArrayInputStream(baos.toByteArray());
			
		} catch (EXIException | SAXException e) {
			// something went wrong with EXI --> reset & try "plain-text" json
			bais.reset();
		}
    	
    	Object jsonld = JsonUtils.fromInputStream(bais);
    	return mapJson(jsonld);
    }

    public static ThingDescription fromFile(String fname)
            throws FileNotFoundException, IOException {
    	Path path = Paths.get(fname);
    	byte[] data = Files.readAllBytes(path);
    	return fromBytes(data);
    	
//    	Object jsonld = JsonUtils.fromReader(new FileReader(fname));
//        return mapJson(jsonld);
    }
    
    /**
     * reshapes the input JSON-LD object using the standard
     * Thing Description context and having the thing description
     * resource as object root.
     * @param jsonld UTF-8 encoded JSON-LD object
     * @param format triple serialization format
     * @return the reshaped JSON-LD object
     * @throws IOException
     */
    public static String reshape(byte[] data) throws IOException {
      ObjectMapper om = new ObjectMapper();
      
      try {
        Object jsonld = JsonUtils.fromInputStream(new ByteArrayInputStream(data));
        // TODO put the frame online instead
        Object frame = om.readValue("{\"http://www.w3c.org/wot/td#hasMetadata\":{}}", HashMap.class);
        
        jsonld = JsonLdProcessor.frame(jsonld, frame, new JsonLdOptions());
        return compactJson(jsonld).toString();
      } catch (JsonLdError e) {
        throw new IOException("Can't reshape triples", e);
      }
    }

}
