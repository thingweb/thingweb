package de.thingweb.util.typesystem;

import static org.junit.Assert.*;

import org.junit.Test;

public class TypeSystemCheckerTest {

	@Test
	public void testIsXmlSchemaType1() {
		String type = "xsd:integer";
		assertTrue(TypeSystemChecker.isXmlSchemaType(type));
		assertTrue(TypeSystemChecker.getTypeSystem(type) == TypeSystem.XML_SCHEMA_DATATYPES);
	}
	
	@Test
	public void testIsXmlSchemaType2() {
		String type = " xsd:byte";
		assertTrue(TypeSystemChecker.isXmlSchemaType(type));
		assertTrue(TypeSystemChecker.getTypeSystem(type) == TypeSystem.XML_SCHEMA_DATATYPES);
	}
	
	@Test
	public void testIsXmlSchemaTypeFail1() {
		String type = "{ \"type\": \"integer\" }";
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
		String type = "{ \"type\": \"number\" }";
		assertTrue(TypeSystemChecker.isJsonSchemaType(type));
		assertTrue(TypeSystemChecker.getTypeSystem(type) == TypeSystem.JSON_SCHEMA);
	}
	
	@Test
	public void testIsJsonSchemaType2() {
		String type = "{\n    \"$schema\": \"http://json-schema.org/draft-04/schema#\",\n    \"title\": \"Product\",\n    \"description\": \"A product from Acme\'s catalog\",\n    \"type\": \"object\",\n    \"properties\": {\n        \"id\": {\n            \"description\": \"The unique identifier for a product\",\n            \"type\": \"integer\"\n        }\n    },\n    \"required\": [\"id\"]\n}";
		assertTrue(TypeSystemChecker.isJsonSchemaType(type));
		assertTrue(TypeSystemChecker.getTypeSystem(type) == TypeSystem.JSON_SCHEMA);
	}
	
	@Test
	public void testIsJsonSchemaTypeFail1() {
		String type = "{ \"id\": \"ABC\"} ";
		assertFalse(TypeSystemChecker.isJsonSchemaType(type));
		assertTrue(TypeSystemChecker.getTypeSystem(type) == TypeSystem.UNKNOWN);
	}
	
	@Test
	public void testIsSchemaOrg1() {
		String type = " https://schema.org/arrivalTime";
		assertTrue(TypeSystemChecker.isSchemaOrg(type));
		assertTrue(TypeSystemChecker.getTypeSystem(type) == TypeSystem.SCHEMA_ORG);
	}
	
	@Test
	public void testIsSchemaOrg2() {
		String type = "http://schema.org/Thing ";
		assertTrue(TypeSystemChecker.isSchemaOrg(type));
		assertTrue(TypeSystemChecker.getTypeSystem(type) == TypeSystem.SCHEMA_ORG);
	}
	
	@Test
	public void testIsSchemaOrgFail1() {
		String type = "xsd:byte";
		assertFalse(TypeSystemChecker.isSchemaOrg(type));
		assertTrue(TypeSystemChecker.getTypeSystem(type) == TypeSystem.XML_SCHEMA_DATATYPES);
	}

	
	@Test
	public void testIsSchemaOrgFail2() {
		String type = "foo";
		assertFalse(TypeSystemChecker.isSchemaOrg(type));
		assertTrue(TypeSystemChecker.getTypeSystem(type) == TypeSystem.UNKNOWN);
	}

}
