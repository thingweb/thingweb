package de.thingweb.typesystem.jsonschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Some basic factories for the common json types used in WoT
 * to be extended with check-functions and conversion for XSD
 * Created by Johannes on 02.09.2016.
 */
public class JsonTypeHelper {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static JsonNode stringType() {
        return mapper.createObjectNode().put("type","string");
    }
    public static JsonNode scriptType() {
        return mapper.createObjectNode().put("type","javascript");
    }

    public static JsonNode booleanType() {
        return mapper.createObjectNode().put("type","boolean");
    }

    public static JsonNode numberType() {
        return mapper.createObjectNode().put("type","number");
    }

    public static JsonNode integerType() {
        return mapper.createObjectNode().put("type","integer");
    }

    public static JsonNode numberType(int min, int max) {
        return mapper.createObjectNode()
                .put("type","number")
                .put("min",min)
                .put("max",max);
    }

}
