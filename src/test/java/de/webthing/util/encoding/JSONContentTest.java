package de.webthing.util.encoding;

import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import de.webthing.thing.Content;
import de.webthing.thing.MediaType;

public class JSONContentTest {

	// {
    //	"id": 1,
    //	"name": "A green door",
    //	"price": 12.50,
    //	"tags": ["home", "green"]
	// }
	String s = "{"
			+ "\"id\": 1,"
			+ "\"name\": \"A green door\","
			+ "\"price\": 12.50,"
			+ "\"tags\": [\"home\", \"green\"]"
			+ "}";
	
	@Test
	public void testObject1() throws UnsupportedEncodingException {
		// as text
		Content content = JSONContent.getContent(s);
		JSONObject obj = JSONContent.parseJSON(content);
		Object o = obj.get(JSONContent.JSON_ROOT);
		assertTrue(o instanceof JSONObject);
		JSONObject oo = (JSONObject) o;
		assertTrue(oo.get("id").equals(1));
		assertTrue(oo.get("name").equals("A green door"));
		assertTrue(oo.get("price").equals(12.50));
		assertTrue(oo.get("tags") instanceof JSONArray);
		JSONArray oa = (JSONArray) oo.get("tags") ;
		assertTrue(oa.length() == 2);
		assertTrue(oa.get(0).equals("home"));
		assertTrue(oa.get(1).equals("green"));
	}
	
	@Test
	public void testObject2() throws UnsupportedEncodingException {
		// as Object
		Content content = JSONContent.getContent(new JSONObject(s));
		JSONObject obj = JSONContent.parseJSON(content);
		Object o = obj.get(JSONContent.JSON_ROOT);
		assertTrue(o instanceof JSONObject);
		JSONObject oo = (JSONObject) o;
		assertTrue(oo.get("id").equals(1));
		assertTrue(oo.get("name").equals("A green door"));
		assertTrue(oo.get("price").equals(12.50));
		assertTrue(oo.get("tags") instanceof JSONArray);
		JSONArray oa = (JSONArray) oo.get("tags") ;
		assertTrue(oa.length() == 2);
		assertTrue(oa.get(0).equals("home"));
		assertTrue(oa.get(1).equals("green"));
	}
	
	@Test
	public void testNumber1() throws UnsupportedEncodingException {
		String s = "123";
		JSONObject obj = JSONContent.parseJSON(new Content(s.getBytes(), MediaType.APPLICATION_JSON));
		Object on = obj.get(JSONContent.JSON_ROOT);
		assertTrue(on instanceof Integer);
		assertTrue(new Integer(123).equals(on));
	}

}
