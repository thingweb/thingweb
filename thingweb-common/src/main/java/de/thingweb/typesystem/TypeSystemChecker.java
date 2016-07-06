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

package de.thingweb.typesystem;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

/**
 * The idea is to provide a simple mean to decide which type-system is used in a
 * TD (e.g., XML schema datatype, JSON schema, et cetera).
 * 
 * @author https://github.com/danielpeintner
 * 
 *
 */
public class TypeSystemChecker {
	
	public static TypeSystem getTypeSystem(JsonNode type) {
		TypeSystem ts = TypeSystem.UNKNOWN;
		
		if(isXmlSchemaType(type)) {
			ts = TypeSystem.XML_SCHEMA_DATATYPES;
		} else if(isSchemaOrg(type)) {
			ts = TypeSystem.SCHEMA_ORG;
		} else if(isJsonSchemaType(type)) {
			ts = TypeSystem.JSON_SCHEMA;
		}
		return ts;
	}

	public static boolean isXmlSchemaType(JsonNode type) {
		boolean isXsd = false;
		
		if (type != null && type.getNodeType() == JsonNodeType.STRING) {
			// very naive and simple for now
			isXsd = type.asText().trim().startsWith("xsd:");
		}

		return isXsd;
	}

	public static boolean isSchemaOrg(JsonNode type) {
		boolean isSchemaOrg = false;
		
		// TODO check how schema.org types are referenced/used
		return isSchemaOrg;
	}
	
	/** JSON Schema defines seven primitive types for JSON values (see http://json-schema.org/latest/json-schema-core.html#anchor8) */
	static final List<String> JSON_SCHEMA_PRIMITIVE_TYPES = Arrays.asList("array", "boolean", "integer", "number", "null", "object", "string");

	public static boolean isJsonSchemaType(JsonNode type) {
		boolean isJsonSchema = false;
		
		// TODO use JSON schema parser
		if (type != null && type.getNodeType() == JsonNodeType.OBJECT) {
			JsonNode value = type.findValue("type");
			if(value != null) {
				isJsonSchema =  JSON_SCHEMA_PRIMITIVE_TYPES.contains(value.asText());
			}
		}
		
		return isJsonSchema;
		
	}

}
