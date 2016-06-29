package de.thingwebtypesystem.jsonschema;

import static org.junit.Assert.*;

import org.junit.Test;

import de.thingweb.typesystem.jsonschema.JsonArray;
import de.thingweb.typesystem.jsonschema.JsonInteger;
import de.thingweb.typesystem.jsonschema.JsonNumber;
import de.thingweb.typesystem.jsonschema.JsonObject;
import de.thingweb.typesystem.jsonschema.JsonSchemaException;
import de.thingweb.typesystem.jsonschema.JsonSchemaType;
import de.thingweb.typesystem.jsonschema.JsonType;
import de.thingweb.typesystem.jsonschema.PrimitiveType;

public class JsonSchemaTypeTest {

	@Test
	public void testNumber1() throws JsonSchemaException {
		String type = "{\"type\": \"number\"}";
		
		assertTrue(JsonSchemaType.isSimpleType(type));
		assertFalse(JsonSchemaType.isComposedType(type));
		
		assertTrue(JsonSchemaType.getJsonType(type).getPrimitiveType() == PrimitiveType.NUMBER);
	}
	
	@Test
	public void testNull1() throws JsonSchemaException {
		String type = "{\"type\": \"null\"}";
		
		assertTrue(JsonSchemaType.isSimpleType(type));
		assertFalse(JsonSchemaType.isComposedType(type));
		
		assertTrue(JsonSchemaType.getJsonType(type).getPrimitiveType() == PrimitiveType.NULL);
	}
	
	@Test
	public void testObject1() throws JsonSchemaException {
		String type = "{\r\n\t\"title\": \"Example Schema\",\r\n\t\"type\": \"object\",\r\n\t\"properties\": {\r\n\t\t\"firstName\": {\r\n\t\t\t\"type\": \"string\"\r\n\t\t},\r\n\t\t\"lastName\": {\r\n\t\t\t\"type\": \"string\"\r\n\t\t},\r\n\t\t\"age\": {\r\n\t\t\t\"description\": \"Age in years\",\r\n\t\t\t\"type\": \"integer\",\r\n\t\t\t\"minimum\": 0\r\n\t\t}\r\n\t},\r\n\t\"required\": [\"firstName\", \"lastName\"]\r\n}";
		
		assertFalse(JsonSchemaType.isSimpleType(type));
		assertTrue(JsonSchemaType.isComposedType(type));
		
		assertTrue(JsonSchemaType.getJsonType(type).getPrimitiveType() == PrimitiveType.OBJECT);
		
		JsonObject jo = (JsonObject)JsonSchemaType.getJsonType(type);
		
		// content firstName, lastName, age
		assertTrue(jo.getProperties().size() == 3);
		
		JsonType jtFirstName = jo.getProperty("firstName");
		assertTrue(jtFirstName.getPrimitiveType() == PrimitiveType.STRING);
		
		JsonType jtLastName = jo.getProperty("lastName");
		assertTrue(jtLastName.getPrimitiveType() == PrimitiveType.STRING);
		
		JsonType jtAge = jo.getProperty("age");
		assertTrue(jtAge.getPrimitiveType() == PrimitiveType.INTEGER);
		JsonInteger ji = (JsonInteger) jtAge;
		assertTrue(ji.getMinimum() == 0);
		assertTrue(ji.getMaximum() == null);
		
		//  "required": ["firstName", "lastName"]
		assertTrue(jo.getRequireds().contains("firstName"));
		assertTrue(jo.getRequireds().contains("firstName"));
		assertFalse(jo.getRequireds().contains("age"));
	}
	
	@Test
	public void testIntegerMaxMin1() throws JsonSchemaException {
		String type = "{\"type\": \"integer\", \"minimum\": 2, \"maximum\": 200 }";
		
		assertTrue(JsonSchemaType.isSimpleType(type));
		assertFalse(JsonSchemaType.isComposedType(type));
		
		assertTrue(JsonSchemaType.getJsonType(type).getPrimitiveType() == PrimitiveType.INTEGER);
		JsonInteger ji = (JsonInteger) JsonSchemaType.getJsonType(type);
		assertTrue(ji.getMinimum() == 2);
		assertTrue(ji.getMaximum() == 200);
		assertFalse(ji.isExclusiveMinimum());
		assertFalse(ji.isExclusiveMaximum());
	}
	
	@Test
	public void testIntegerMaxMin2() throws JsonSchemaException {
		String type = "{\"type\": \"integer\", \"minimum\": 2, \"maximum\": 200, \"exclusiveMaximum\": true }";
		
		assertTrue(JsonSchemaType.isSimpleType(type));
		assertFalse(JsonSchemaType.isComposedType(type));
		
		assertTrue(JsonSchemaType.getJsonType(type).getPrimitiveType() == PrimitiveType.INTEGER);
		JsonInteger ji = (JsonInteger) JsonSchemaType.getJsonType(type);
		assertTrue(ji.getMinimum() == 2);
		assertTrue(ji.getMaximum() == 200);
		assertFalse(ji.isExclusiveMinimum());
		assertTrue(ji.isExclusiveMaximum());
	}
	
	
	@Test
	public void testNumberMaxMin1() throws JsonSchemaException {
		String type = "{\"type\": \"number\", \"minimum\": 1.2, \"maximum\": 3.4 }";
		
		assertTrue(JsonSchemaType.isSimpleType(type));
		assertFalse(JsonSchemaType.isComposedType(type));
		
		assertTrue(JsonSchemaType.getJsonType(type).getPrimitiveType() == PrimitiveType.NUMBER);
		JsonNumber jn = (JsonNumber) JsonSchemaType.getJsonType(type);
		assertTrue(jn.getMinimum() == 1.2);
		assertTrue(jn.getMaximum() == 3.4);
		assertFalse(jn.isExclusiveMinimum());
		assertFalse(jn.isExclusiveMaximum());
	}
	
	@Test
	public void testNumberMaxMin2() throws JsonSchemaException {
		String type = "{\"type\": \"number\", \"minimum\": 1.2, \"maximum\": 3.4, \"exclusiveMinimum\": true }";
		
		assertTrue(JsonSchemaType.isSimpleType(type));
		assertFalse(JsonSchemaType.isComposedType(type));
		
		assertTrue(JsonSchemaType.getJsonType(type).getPrimitiveType() == PrimitiveType.NUMBER);
		JsonNumber jn = (JsonNumber) JsonSchemaType.getJsonType(type);
		assertTrue(jn.getMinimum() == 1.2);
		assertTrue(jn.getMaximum() == 3.4);
		assertTrue(jn.isExclusiveMinimum());
		assertFalse(jn.isExclusiveMaximum());
	}
	
	
	@Test
	public void testArray1() throws JsonSchemaException {
		String type = "{\"type\": \"array\", \"items\": {\"type\": \"string\"}}";
		
		assertFalse(JsonSchemaType.isSimpleType(type));
		assertTrue(JsonSchemaType.isComposedType(type));
		
		assertTrue(JsonSchemaType.getJsonType(type).getPrimitiveType() == PrimitiveType.ARRAY);
		JsonArray ja = (JsonArray) JsonSchemaType.getJsonType(type);
		assertTrue(ja.getItemsType().getPrimitiveType() == PrimitiveType.STRING);
	}

}
