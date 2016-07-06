package de.thingweb.typesystem;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import de.thingweb.typesystem.TypeSystem;
import de.thingweb.typesystem.TypeSystemChecker;
import de.thingweb.typesystem.jsonschema.JsonObject;
import de.thingweb.typesystem.jsonschema.JsonType;

public class TypeSystemCheckerTest {

	@Test
	public void testIsXmlSchemaType1() {
		JsonNode type = new TextNode("xsd:integer");
		assertTrue(TypeSystemChecker.isXmlSchemaType(type));
		assertTrue(TypeSystemChecker.getTypeSystem(type) == TypeSystem.XML_SCHEMA_DATATYPES);
	}
	
	@Test
	public void testIsXmlSchemaType2() {
		JsonNode type = new TextNode(" xsd:byte");
		assertTrue(TypeSystemChecker.isXmlSchemaType(type));
		assertTrue(TypeSystemChecker.getTypeSystem(type) == TypeSystem.XML_SCHEMA_DATATYPES);
	}
	
	@Test
	public void testIsXmlSchemaTypeFail1() {
		JsonNodeFactory jnf = new JsonNodeFactory(false);
		ObjectNode type = jnf.objectNode();
		type.put("type", jnf.textNode("integer"));
	
		assertFalse(TypeSystemChecker.isXmlSchemaType(type));
		assertTrue(TypeSystemChecker.getTypeSystem(type) == TypeSystem.JSON_SCHEMA);
	}
	
//	// TODO properly test unknown XML schema datatypes
//	@Test
//	public void testIsXmlSchemaTypeFail2() {
//		assertFalse(TypeSystemChecker.isXmlSchemaType(" xsd:unknown"));
//	}

	@Test
	public void testIsJsonSchemaType1() {
		JsonNodeFactory jnf = new JsonNodeFactory(false);
		ObjectNode type = jnf.objectNode();
		type.put("type", jnf.textNode("number"));
		
		assertTrue(TypeSystemChecker.isJsonSchemaType(type));
		assertTrue(TypeSystemChecker.getTypeSystem(type) == TypeSystem.JSON_SCHEMA);
	}
	
	@Test
	public void testIsJsonSchemaType2() throws JsonParseException, JsonMappingException, IOException {
		String stype = "{\r\n" + 
				"    \"$schema\": \"http://json-schema.org/draft-04/schema#\",\r\n" + 
				"    \"title\": \"Product\",\r\n" + 
				"    \"description\": \"A product from Acme's catalog\",\r\n" + 
				"    \"type\": \"object\",\r\n" + 
				"    \"properties\": {\r\n" + 
				"        \"id\": {\r\n" + 
				"            \"description\": \"The unique identifier for a product\",\r\n" + 
				"            \"type\": \"integer\"\r\n" + 
				"        }\r\n" + 
				"    },\r\n" + 
				"    \"required\": [\"id\"]\r\n" + 
				"}";
		ObjectMapper mapper = new ObjectMapper();
		JsonNode type = mapper.readValue(new StringReader(stype), JsonNode.class);
		
		assertTrue(TypeSystemChecker.isJsonSchemaType(type));
		assertTrue(TypeSystemChecker.getTypeSystem(type) == TypeSystem.JSON_SCHEMA);
	}
	
	@Test
	public void testIsJsonSchemaTypeFail1() {
		JsonNodeFactory jnf = new JsonNodeFactory(false);
		ObjectNode type = jnf.objectNode();
		type.put("id", jnf.textNode("ABC"));
		
		assertFalse(TypeSystemChecker.isJsonSchemaType(type));
		assertTrue(TypeSystemChecker.getTypeSystem(type) == TypeSystem.UNKNOWN);
	}
	
//	@Test
//	// TODO how does a schema.org type look like
//	public void testIsSchemaOrg1() {
//		JsonNode type = new TextNode(" https://schema.org/arrivalTime");
//		assertTrue(TypeSystemChecker.isSchemaOrg(type));
//		assertTrue(TypeSystemChecker.getTypeSystem(type) == TypeSystem.SCHEMA_ORG);
//	}
//	
//	@Test
//	// TODO how does a schema.org type look like
//	public void testIsSchemaOrg2() {
//		JsonNode type = new TextNode("http://schema.org/Thing ");
//		assertTrue(TypeSystemChecker.isSchemaOrg(type));
//		assertTrue(TypeSystemChecker.getTypeSystem(type) == TypeSystem.SCHEMA_ORG);
//	}
	
	@Test
	public void testIsSchemaOrgFail1() {
		JsonNode type = new TextNode("xsd:byte");
		assertFalse(TypeSystemChecker.isSchemaOrg(type));
		assertTrue(TypeSystemChecker.getTypeSystem(type) == TypeSystem.XML_SCHEMA_DATATYPES);
	}

	
	@Test
	public void testIsSchemaOrgFail2() {
		JsonNode type = new TextNode("foo");
		assertFalse(TypeSystemChecker.isSchemaOrg(type));
		assertTrue(TypeSystemChecker.getTypeSystem(type) == TypeSystem.UNKNOWN);
	}

}
