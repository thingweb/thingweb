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
