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

package de.thingweb.typesystem.jsonschema;

import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

public class JsonSchemaType {
	
	static final List<String> SIMPLE_TYPES = Arrays.asList("boolean", "integer", "number", "null", "string");
	
	static final List<String> COMPOSED_TYPES = Arrays.asList("array", "object");

	
	public static boolean isSimpleType(String type) {
		return isOfType(type, SIMPLE_TYPES);
	}
	
	public static boolean isComposedType(String type) {
		return isOfType(type, COMPOSED_TYPES);
	}
	
	private static boolean isOfType(String type, List<String> types) {
		boolean isOfType = false;
		
		try {
			// TODO use JSON schema parser
			if (type != null) {
				
				ObjectMapper mapper = new ObjectMapper();
				JsonNode valueType = mapper.readValue(new StringReader(type), JsonNode.class);
				if(valueType != null) {
					JsonNode value = valueType.findValue("type");
					if(value != null) {
						isOfType =  types.contains(value.asText());
					}
				}
			}
		} catch (Exception e) {
			// failure --> no JSON schema
		}
		
		return isOfType;
	}
	
	public static JsonType getJsonType(String type) throws JsonSchemaException {
		JsonType jtype = null;
		try {
			// TODO use JSON schema parser
			if (type != null) {
				
				ObjectMapper mapper = new ObjectMapper();
				JsonNode jsonSchemaNode = mapper.readValue(new StringReader(type), JsonNode.class);
				jtype = getJsonType(jsonSchemaNode);
			}
		} catch (Exception e) {
			// failure --> no JSON schema
		}
		
		return jtype;
	}
	
	public static JsonType getJsonType(JsonNode jsonSchemaNode) throws JsonSchemaException {
		JsonType jtype = null;
		
		if(jsonSchemaNode != null) {
			JsonNode typeNode = jsonSchemaNode.findValue("type");
			if(typeNode != null) {
				switch(typeNode.asText()) {
				case "boolean":
					jtype = new JsonBoolean();
					break;
				case "integer":
				case "number":
					boolean exclusiveMinimum = false;
					JsonNode exclusiveMinimumNode = jsonSchemaNode.findValue("exclusiveMinimum");
					if(exclusiveMinimumNode != null && exclusiveMinimumNode.getNodeType() == JsonNodeType.BOOLEAN) {
						exclusiveMinimum = exclusiveMinimumNode.asBoolean();
					}
					boolean exclusiveMaximum = false;
					JsonNode exclusiveMaximumNode = jsonSchemaNode.findValue("exclusiveMaximum");
					if(exclusiveMaximumNode != null && exclusiveMaximumNode.getNodeType() == JsonNodeType.BOOLEAN) {
						exclusiveMaximum = exclusiveMaximumNode.asBoolean();
					}
					
					if ("integer".equals(typeNode.asText())) {
						jtype = new JsonInteger();
						JsonNode minimumNode = jsonSchemaNode.findValue("minimum");
						if(minimumNode != null && minimumNode.getNodeType() == JsonNodeType.NUMBER) {
							((JsonInteger)jtype).setMinimum(minimumNode.asInt());
						}
						JsonNode maximumNode = jsonSchemaNode.findValue("maximum");
						if(maximumNode != null && maximumNode.getNodeType() == JsonNodeType.NUMBER) {
							((JsonInteger)jtype).setMaximum(maximumNode.asInt());
						}
					} else {
						assert("number".equals(typeNode.asText()));
						
						jtype = new JsonNumber();
						JsonNode minimumNode = jsonSchemaNode.findValue("minimum");
						if(minimumNode != null && minimumNode.getNodeType() == JsonNodeType.NUMBER) {
							((JsonNumber)jtype).setMinimum(minimumNode.asDouble());
						}
						JsonNode maximumNode = jsonSchemaNode.findValue("maximum");
						if(maximumNode != null && maximumNode.getNodeType() == JsonNodeType.NUMBER) {
							((JsonNumber)jtype).setMaximum(maximumNode.asDouble());
						}
					}
					
					((AbstractJsonNumeric)jtype).setExclusiveMinimum(exclusiveMinimum);
					((AbstractJsonNumeric)jtype).setExclusiveMaximum(exclusiveMaximum);

					break;
				case "null":
					jtype = new JsonNull();
					break;
				case "string":
					jtype = new JsonString();
					break;
				case "array":
					JsonNode itemsNode = jsonSchemaNode.findValue("items");
					if(itemsNode != null && JsonNodeType.OBJECT == itemsNode.getNodeType()) {
						jtype = new JsonArray(getJsonType(itemsNode));
					} else {
						throw new JsonSchemaException("items not object");
					}
					
					break;
				case "object":
					JsonNode propertiesNode = jsonSchemaNode.findValue("properties");
					if(propertiesNode != null && JsonNodeType.OBJECT == propertiesNode.getNodeType()) {
						Iterator<Entry<String, JsonNode>> iter = propertiesNode.fields();
						Map<String, JsonType> properties = new HashMap<String, JsonType>();
						while(iter.hasNext()) {
							Entry<String, JsonNode> e = iter.next();
							JsonNode nodeProp = e.getValue();
							properties.put(e.getKey(), getJsonType(nodeProp));
						}
						jtype = new JsonObject(properties);
					} else {
						throw new JsonSchemaException("Properties not object");
					}
					// required
					JsonNode requiredNode = jsonSchemaNode.findValue("required");
					if(requiredNode != null && JsonNodeType.ARRAY == requiredNode.getNodeType()) {
						ArrayNode an = (ArrayNode) requiredNode;
						Iterator<JsonNode> iterReq = an.elements();
						while(iterReq.hasNext()) {
							JsonNode nreq = iterReq.next();
							if(JsonNodeType.STRING == nreq.getNodeType()) {
								((JsonObject)jtype).addRequired(nreq.asText());
							} else {
								throw new JsonSchemaException("Unexpected required node: " + nreq);
							}
						}
					}
					break;
				}
			}
		}
		
		return jtype;
	}
	


}
