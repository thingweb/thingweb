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

package de.webthing.util.encoding;

import de.webthing.thing.Content;
import de.webthing.thing.MediaType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertTrue;

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
