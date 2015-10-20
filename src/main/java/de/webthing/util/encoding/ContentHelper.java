package de.webthing.util.encoding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.webthing.thing.Content;
import de.webthing.thing.MediaType;

import java.io.IOException;
import java.util.InputMismatchException;

/**
 * Created by Johannes on 20.10.2015.
 */
public class ContentHelper {

    public static final ObjectMapper mapper = new ObjectMapper();

    public static Object parse(Content c, Class<?> expected) throws IOException {
        switch (c.getMediaType()) {
            case TEXT_PLAIN:
                return new String(c.getContent());
            case APPLICATION_JSON:
                return parseJSON(c.getContent(), expected);
            case APPLICATION_XML:
            case APPLICATION_EXI:
            case UNDEFINED:
            default:
                throw new InputMismatchException("406 Not-Acceptable");
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
}