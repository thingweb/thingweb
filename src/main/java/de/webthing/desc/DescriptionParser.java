package de.webthing.desc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jsonldjava.core.JsonLdApi;
import com.github.jsonldjava.core.JsonLdConsts;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.core.JsonLdUtils;
import com.github.jsonldjava.utils.JsonLdUrl;
import com.github.jsonldjava.utils.JsonUtils;

import de.webthing.desc.pojo.InteractionDescription;
import de.webthing.desc.pojo.Metadata;
import de.webthing.desc.pojo.Protocol;
import de.webthing.desc.pojo.ThingDescription;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Johannes on 02.09.2015.
 */
public class DescriptionParser {

    static {
	try {
	    context = JsonUtils.fromURL(new URL(
		    "http://w3c.github.io/wot/w3c-wot-td-context.jsonld"));
	} catch (IOException e) {
	    e.printStackTrace();
	    context = null;
	}
    }

    // default JSON-LD context for TDs
    private static Object context;

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
