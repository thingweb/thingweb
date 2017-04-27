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

package de.thingweb.desc;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.json.EXIforJSONGenerator;

import de.thingweb.thing.Action;
import de.thingweb.thing.Event;
import de.thingweb.thing.Metadata;
import de.thingweb.thing.Property;
import de.thingweb.thing.Thing;

public class ThingDescriptionParserTest {
	
	// Note: some test-cases are ignored given that the online resource (URL) does not exist anymore

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Ignore @Test
    public void testFromURLDoor() throws JsonParseException, IOException {
    	URL jsonld = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/door.jsonld");
    	ThingDescriptionParser.fromURL(jsonld);
    	// TODO any further checks?
    }
    
    @Ignore @Test
    public void testFromURLLed() throws JsonParseException, IOException {
    	URL jsonld = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/led.jsonld");
    	ThingDescriptionParser.fromURL(jsonld);
    	// TODO any further checks?
    }
    
    @Ignore @Test
	public void testFromURLLedEXI4JSON() throws JsonParseException, IOException, EXIException {
		URL jsonld = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/led.jsonld");

		InputStream is = jsonld.openStream();

		File f = File.createTempFile("jsonld", "e4j");
		OutputStream os = new FileOutputStream(f);

		EXIFactory ef = DefaultEXIFactory.newInstance();
		ef.setSharedStrings(ThingDescriptionParser.SHARED_STRINGS_EXI_FOR_JSON);
		
		EXIforJSONGenerator e4j = new EXIforJSONGenerator(ef);
		e4j.generate(is, os);

		is.close();
		os.close();

		ThingDescriptionParser.fromFile(f.getAbsolutePath());
		// TODO any further checks?
	}
    
    
    
    @Test
    public void testFromURLLed_v02() throws JsonParseException, IOException {
    	URL jsonld = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/led_v02.jsonld");
    	try {
    		ThingDescriptionParser.fromURL(jsonld);
    		// TODO are not recognized fields are ignored
//    	    fail();
    	} catch (IOException e) {
    		// OK, expect failure
    	}
    }
    
    @Ignore @Test
    public void testFromURLOutlet() throws JsonParseException, IOException {
    	URL jsonld = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/outlet.jsonld");
    	ThingDescriptionParser.fromURL(jsonld);
    	// TODO any further checks?
    }
    
    @Ignore @Test
    public void testFromURLWeather() throws JsonParseException, IOException {
    	URL jsonld = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/weather.jsonld");
    	ThingDescriptionParser.fromURL(jsonld);
    	// TODO any further checks?
    }
    
    @Test
    public void testLocalStrangeCharacters() throws JsonParseException, IOException {
    	String foo = "{\"metadata\":{\"name\":\"Ugly strange n\u00E4ime\",\"protocols\":{\"CoAP\":{\"uri\":\"coap://MD1EWQUC/things/ugly+strange+n%c3%a4ime\",\"priority\":1},\"HTTP\":{\"uri\":\"http://MD1EWQUC:8080/things/ugly+strange+n%c3%a4ime\",\"priority\":2}},\"encodings\":[\"JSON\"]},\"interactions\":[{\"@type\":\"Property\",\"name\":\"not url komp\u00E4tibel\",\"writable\":false,\"outputData\":\"xsd:string\"},{\"@type\":\"Action\",\"name\":\"wierdly named \u00E4ktschn\",\"inputData\":null,\"outputData\":\"\"}],\"@context\":\"http://w3c.github.io/wot/w3c-wot-td-context.jsonld\"}";
   		@SuppressWarnings("unused")
		Thing td = ThingDescriptionParser.fromBytes(foo.getBytes());
   		// TODO any further checks?
    }
    
 
    @Test
    public void testFromFile() {
      String happyPath = "jsonld" + File.separator + "led.v2.jsonld";
      // should use parser for deprecated Thing Descriptions
      String happyPathOld = "jsonld" + File.separator + "led.jsonld";
      // should fail (required field not found)
      String invalid = "jsonld" + File.separator + "led.v2.invalid.jsonld";

      try {
          ThingDescriptionParser.fromFile(happyPath);
      } catch (Exception e) {
          e.printStackTrace();
          fail();
      }
      
      try {
          ThingDescriptionParser.fromFile(happyPathOld);
      } catch (Exception e) {
          e.printStackTrace();
          fail();
      }
      
      try {
          ThingDescriptionParser.fromFile(invalid);
          fail();
      } catch (IOException e) {
          if (e instanceof IOException) {
            // as expected
          } else {
          	e.printStackTrace();
          	fail();
          }
      }
    }
    
    
	// TODO shall we test round tripping of any node? e.g, "actuator:unit": "actuator:ms" as part of inputData
	
    // String filename = "jsonld" + File.separator + "led.v2.plain.jsonld";
    String tdRoundtrip1 = "{\r\n" + 
    		"  \"@context\": [\r\n" + 
    		"    \"http://w3c.github.io/wot/w3c-wot-td-context.jsonld\",\r\n" + 
    		"    { \"actuator\": \"http://example.org/actuator#\" }\r\n" + 
    		"  ],\r\n" + 
    		"  \"@type\": \"Thing\",\r\n" + 
    		"  \"name\": \"MyLEDThing\",\r\n" + 
    		"  \"uris\": [\r\n" + 
    		"    \"coap://myled.example.com:5683/\",\r\n" + 
    		"    \"http://mything.example.com:8080/myled/\"\r\n" + 
    		"  ],\r\n" + 
    		"  \"encodings\": [ \"JSON\",\"EXI\"],\r\n" + 
    		"  \"security\": {\r\n" + 
    		"    \"cat\": \"token:jwt\",\r\n" + 
    		"    \"alg\": \"HS256\",\r\n" + 
    		"    \"as\": \"https://authority-issuing.example.org\"\r\n" + 
    		"  },\r\n" + 
    		"  \"properties\": [\r\n" + 
    		"    {\r\n" + 
    		"      \"@type\": \"actuator:onOffStatus\",\r\n" + 
    		"      \"name\": \"status\",\r\n" + 
    		"      \"valueType\": { \"type\": \"boolean\" },\r\n" + 
    		"      \"writable\": true,\r\n" + 
    		"      \"hrefs\": [ \"pwr\", \"status\" ]\r\n" + 
    		"    }\r\n" + 
    		"  ],\r\n" + 
    		"  \"actions\": [\r\n" + 
    		"    {\r\n" + 
    		"      \"@type\": \"actuator:fadeIn\",\r\n" + 
    		"      \"name\": \"fadeIn\",\r\n" + 
    		"      \"inputData\": {\r\n" + 
    		"        \"valueType\": { \"type\": \"integer\" }"
//    		+ ",\r\n" + 
//    		"        \"actuator:unit\": \"actuator:ms\"\r\n"
    		+ 
    		"      },\r\n" + 
    		"      \"hrefs\": [\"in\", \"led/in\"  ]\r\n" + 
    		"    },\r\n" + 
    		"    {\r\n" + 
    		"      \"@type\": \"actuator:fadeOut\",\r\n" + 
    		"      \"name\": \"fadeOut\",\r\n" + 
    		"      \"inputData\": {\r\n" + 
    		"        \"valueType\": { \"type\": \"integer\" }"
//    		+ ",\r\n" + 
//    		"        \"actuator:unit\": \"actuator:ms\"\r\n"
    		+ 
    		"      },\r\n" + 
    		"      \"hrefs\": [\"out\", \"led/out\" ]\r\n" + 
    		"    }\r\n" + 
    		"  ],\r\n" + 
    		"  \"events\": [\r\n" + 
    		"    {\r\n" + 
    		"      \"@type\": \"actuator:alert\",\r\n" + 
    		"      \"name\": \"criticalCondition\",\r\n" + 
    		"      \"valueType\": { \"type\": \"string\" },\r\n" + 
    		"      \"hrefs\": [ \"ev\", \"alert\" ]\r\n" + 
    		"    }\r\n" + 
    		"  ]\r\n" + 
    		"}";
    
    @Test
    public void testRoundtrip1() throws Exception
    {
      ObjectMapper mapper = new ObjectMapper();
      
      JsonNode original = mapper.readValue(tdRoundtrip1, JsonNode.class);
      final ThingDescriptionVersion tdVersion = ThingDescriptionVersion.VERSION_1;
      JsonNode generated = mapper.readValue(ThingDescriptionParser.toBytes(ThingDescriptionParser.fromBytes(tdRoundtrip1.getBytes()), tdVersion), JsonNode.class);
      
      assertTrue(original.equals(generated));
    }
    
    
    @Test
    public void testRoundtrip1_ToV2() throws Exception
    {
      ObjectMapper mapper = new ObjectMapper();
      
      JsonNode original = mapper.readValue(tdRoundtrip1, JsonNode.class);
      JsonNode generated = mapper.readValue(ThingDescriptionParser.toBytes(ThingDescriptionParser.fromBytes(tdRoundtrip1.getBytes())), JsonNode.class);
      
      // assertTrue(original.equals(generated));
      
      assertTrue(original.get("@context").equals(generated.get("@context")));
      assertTrue(original.get("@type").equals(generated.get("@type")));
      assertTrue(original.get("name").equals(generated.get("name")));
      // TODO multiple base not possible, pick first, other uris net to be expanded in local definitions?
      assertTrue(ThingDescriptionParser.stringOrArray(original.get("uris")).get(0).equals(generated.get("base").asText()));
      assertTrue(original.get("security").equals(generated.get("security")));
      
      assertTrue(!generated.has("properties"));
      assertTrue(!generated.has("actions"));
      assertTrue(!generated.has("events"));
      
      assertTrue(generated.has("interactions"));
      assertTrue(generated.get("interactions").isArray());
      ArrayNode anInteractions = (ArrayNode) generated.get("interactions");
      
      // 1 x prop, 2 x act, 1 x ev
      assertTrue(anInteractions.size() == 4);
      for (final JsonNode interaction : anInteractions) {
    	  List<String> types = ThingDescriptionParser.stringOrArray(interaction.get("@type"));
    	  if(types.contains("Property")) {
    		  assertTrue("status".equals(interaction.get("name").textValue()));
    		  assertTrue(ThingDescriptionParser.stringOrArray(interaction.get("@type")).contains("actuator:onOffStatus"));
    		  assertTrue(interaction.get("outputData").get("valueType").get("type").asText().equals("boolean"));
    		  assertTrue(interaction.get("writable").asBoolean());
    		  assertTrue(interaction.get("links").isArray());
    		  ArrayNode anLinks = (ArrayNode) interaction.get("links");
    		  for(JsonNode link : anLinks) {
    			  // TODO not sure what to do with multiple base uris
    			  if(link.get("href").asText().equals("pwr")) {
    				  assertTrue(link.get("mediaType").asText().equals("JSON"));
    			  } else if(link.get("href").asText().equals("status")) {
    				  assertTrue(link.get("mediaType").asText().equals("EXI"));
    			  } else {
    				  throw new Exception("Unsupported href " + link.get("href"));
    			  }
    		  }
    	  } else if(types.contains("Action")) {
    		  boolean bIn = "fadeIn".equals(interaction.get("name").textValue());
    		  boolean bOut = "fadeOut".equals(interaction.get("name").textValue());
    		  assertTrue(bIn || bOut);
    		  
    		  if(bIn) {
    			  assertTrue(ThingDescriptionParser.stringOrArray(interaction.get("@type")).contains("actuator:fadeIn"));
    			  assertTrue(interaction.get("inputData").get("valueType").get("type").asText().equals("integer"));
        		  ArrayNode anLinks = (ArrayNode) interaction.get("links");
        		  for(JsonNode link : anLinks) {
        			  // TODO not sure what to do with multiple base uris
        			  if(link.get("href").asText().equals("in")) {
        				  assertTrue(link.get("mediaType").asText().equals("JSON"));
        			  } else if(link.get("href").asText().equals("led/in")) {
        				  assertTrue(link.get("mediaType").asText().equals("EXI"));
        			  } else {
        				  throw new Exception("Unsupported href " + link.get("href"));
        			  }
        		  }
    		  } else {
    			  // bOut
    			  assertTrue(ThingDescriptionParser.stringOrArray(interaction.get("@type")).contains("actuator:fadeOut"));
    			  assertTrue(interaction.get("inputData").get("valueType").get("type").asText().equals("integer"));
    			  ArrayNode anLinks = (ArrayNode) interaction.get("links");
        		  for(JsonNode link : anLinks) {
        			  // TODO not sure what to do with multiple base uris
        			  if(link.get("href").asText().equals("out")) {
        				  assertTrue(link.get("mediaType").asText().equals("JSON"));
        			  } else if(link.get("href").asText().equals("led/out")) {
        				  assertTrue(link.get("mediaType").asText().equals("EXI"));
        			  } else {
        				  throw new Exception("Unsupported href " + link.get("href"));
        			  }
        		  }
    		  }
    	  } else if(types.contains("Event")) {
    		  assertTrue("criticalCondition".equals(interaction.get("name").textValue()));
    		  assertTrue(ThingDescriptionParser.stringOrArray(interaction.get("@type")).contains("actuator:alert"));
    		  assertTrue(interaction.get("outputData").get("valueType").get("type").asText().equals("string"));
			  ArrayNode anLinks = (ArrayNode) interaction.get("links");
    		  for(JsonNode link : anLinks) {
    			  // TODO not sure what to do with multiple base uris
    			  if(link.get("href").asText().equals("ev")) {
    				  assertTrue(link.get("mediaType").asText().equals("JSON"));
    			  } else if(link.get("href").asText().equals("alert")) {
    				  assertTrue(link.get("mediaType").asText().equals("EXI"));
    			  } else {
    				  throw new Exception("Unsupported href " + link.get("href"));
    			  }
    		  }
    	  } else {
    		  throw new Exception("Unsupported @type " + types);
    	  }
    	  
      }
    }
    
    
	String tdRoundtrip2 = "{\r\n" + 
			"	\"@context\": {\r\n" + 
			"		\"name\": \"http://www.w3c.org/wot/td#name\",\r\n" + 
			"		\"uris\": \"http://www.w3c.org/wot/td#associatedUri\",\r\n" + 
			"		\"unit\": \"http://purl.oclc.org/NET/ssnx/qu/qu-rec20#unit\",\r\n" + 
			"		\"Thing\": \"http://www.w3c.org/wot/td#Thing\"\r\n" + 
			"	},\r\n" + 
			"	\"name\": \"Test\",\r\n" + 
			"	\"properties\": [],\r\n" + 
			"	\"actions\": [],\r\n" + 
			"	\"events\": []\r\n" + 
			"}";
    
    @Test
    public void testRoundtrip2() throws Exception
    {    	
        ObjectMapper mapper = new ObjectMapper();
        
        JsonNode original = mapper.readValue(tdRoundtrip2, JsonNode.class);
        final ThingDescriptionVersion tdVersion = ThingDescriptionVersion.VERSION_1;
        JsonNode generated = mapper.readValue(ThingDescriptionParser.toBytes(ThingDescriptionParser.fromBytes(tdRoundtrip2.getBytes()), tdVersion), JsonNode.class);
        
        assertTrue(original.equals(generated));   
    }
    
    @Test
    public void testRoundtrip2_ToV2() throws Exception
    {    	
        ObjectMapper mapper = new ObjectMapper();
        
		JsonNode original = mapper.readValue(tdRoundtrip2, JsonNode.class);
        JsonNode generated = mapper.readValue(ThingDescriptionParser.toBytes(ThingDescriptionParser.fromBytes(tdRoundtrip2.getBytes())), JsonNode.class);
        
        assertTrue(original.get("@context").equals(generated.get("@context")));
        assertTrue(original.get("name").equals(generated.get("name")));
        
        assertTrue(!generated.has("properties"));
        assertTrue(!generated.has("actions"));
        assertTrue(!generated.has("events"));
        
        assertTrue(generated.has("interactions"));
        
        // assertTrue("\n" + original + "\n vs. \n" + generated, original.equals(generated));   
        
    }
    
    
	// Version 2
	String tdRoundtrip3 = "{\r\n" + 
			"  \"@context\": [\"http://w3c.github.io/wot/w3c-wot-td-context.jsonld\"],\r\n" + 
			"  \"@type\": \"Thing\",\r\n" + 
			"  \"name\": \"MyTemperatureThing\",\r\n" + 
			"  \"interactions\": [\r\n" + 
			"    {\r\n" + 
			"      \"@type\": [\"Property\"],\r\n" + 
			"      \"name\": \"temperature\",\r\n" + 
			"      \"outputData\": {\"valueType\": { \"type\": \"number\" }},\r\n" + 
			"      \"writable\": false,\r\n" + 
			"      \"links\": [{\r\n" + 
			"        \"href\" : \"coap://mytemp.example.com:5683/temp\",\r\n" + 
			"        \"mediaType\": \"application/json\"\r\n" + 
			"        }]\r\n" + 
			"    }\r\n" + 
			"  ]\r\n" + 
			"}";
    
    @Test
    public void testRoundtrip3() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        
        JsonNode original = mapper.readValue(tdRoundtrip3, JsonNode.class);
        // ThingDescriptionVersion.VERSION_2
        JsonNode generated = mapper.readValue(ThingDescriptionParser.toBytes(ThingDescriptionParser.fromBytes(tdRoundtrip3.getBytes())), JsonNode.class);
        
        assertTrue("\n" + original + "\n vs. \n" + generated, original.equals(generated)); 
    }
    
    @Test
    public void testRoundtrip3_ToV1() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        
        @SuppressWarnings("unused")
		JsonNode original = mapper.readValue(tdRoundtrip3, JsonNode.class);
        final ThingDescriptionVersion tdVersion = ThingDescriptionVersion.VERSION_1;
        JsonNode generated = mapper.readValue(ThingDescriptionParser.toBytes(ThingDescriptionParser.fromBytes(tdRoundtrip3.getBytes()), tdVersion), JsonNode.class);
        
        // assertTrue("\n" + original + "\n vs. \n" + generated, original.equals(generated)); 
        
        assertTrue(generated.has("properties"));
        assertTrue(!generated.has("interactions"));
        
        assertTrue(generated.get("properties").isArray());
        ArrayNode an = (ArrayNode) generated.get("properties");
        assertTrue(an.size() == 1);
        JsonNode jn0 = an.get(0);
        assertTrue(jn0.has("hrefs"));
        assertTrue(jn0.get("hrefs").asText().equals("coap://mytemp.example.com:5683/temp"));
        assertTrue(jn0.has("name"));
        assertTrue(jn0.get("name").asText().equals("temperature"));
        assertTrue(jn0.has("valueType"));
        assertTrue(jn0.get("valueType").has("type"));
        assertTrue(jn0.has("writable"));
        assertTrue(jn0.get("writable").asBoolean() == false);
    }
    
//    @Test
//    public void testReshape() {
//      try {
//        File f = new File("jsonld/outlet_flattened.jsonld");
//        FileReader r = new FileReader(f);
//        char[] buf = new char [(int) f.length()];
//        r.read(buf);
//        r.close();
//        String jsonld = ThingDescriptionParser.reshape(new String(buf).getBytes());
//        // checks that reshaped jsonld is compliant to description parser's impl.
//        ThingDescriptionParser.fromBytes(jsonld.getBytes());
//        // TODO any further checks?
//      } catch (Exception e) {
//        fail(e.getMessage());
//      }
//    }
    
    
//    {
//    	  "@context": ["http://w3c.github.io/wot/w3c-wot-td-context.jsonld"],
//    	  "@type": "Thing",
//    	  "name": "MyTemperatureThing",
//    	  "uris": "coap://www.mytemp.com:5683/",
//    	  "encodings": ["JSON"],
//    	  "properties": [
//    	    {
//    	      "name": "temperature",
//    	      "valueType": {
//    	        "type": "integer",
//    	        "maximum": 13
//    	      },
//    	      "writable": false,
//    	      "hrefs": ["temp"]
//    	    }
//    	  ]
//    	}
    @Test
    public void testJSONSchema1() throws Exception {
    	String json = "{\n  \"@context\": [\"http://w3c.github.io/wot/w3c-wot-td-context.jsonld\"],\n  \"@type\": \"Thing\",\n  \"name\": \"MyTemperatureThing\",\n  \"uris\": \"coap://www.mytemp.com:5683/\",\n  \"encodings\": [\"JSON\"],\n  \"properties\": [\n    {\n      \"name\": \"temperature\",\n      \"valueType\": {\n        \"type\": \"integer\",\n        \"maximum\": 13\n      },\n      \"writable\": false,\n      \"hrefs\": [\"temp\"]\n    }\n  ]\n}";
    	
   		Thing td = ThingDescriptionParser.fromBytes(json.getBytes());
   		// TODO any further checks?
   		assertTrue("No Property temperature", td.getProperty("temperature") != null);
   		Property p = td.getProperty("temperature");
   		assertTrue("No valueType", p.getValueType() != null);
		JsonNode valueType = p.getValueType();
		assertTrue(valueType.findValue("type").asText().equals("integer"));
		assertTrue(valueType.findValue("maximum").asInt() == 13);
    }
    
    @Test
    // CP: Example 2: Semantic Annotations
    public void testJSONSchema2() throws Exception {
    	String json = "{\n\t\"@context\": [\n\t\t\"http://w3c.github.io/wot/w3c-wot-td-context.jsonld\", {\n\t\t\t\"sensor\": \"http://example.org/sensors#\"\n\t\t}\n\t],\n\t\"@type\": \"Thing\",\n\t\"name\": \"MyTemperatureThing\",\n\t\"uris\": [\"coap://www.mytemp.com:5683/\"],\n\t\"encodings\": [\"JSON\"],\n\t\"properties\": [{\n\t\t\"@type\": \"sensor:Temperature\",\n\n\t\t\"name\": \"temperature\",\n\t\t\"sensor:unit\": \"sensor:Celsius\",\n\n\t\t\"valueType\": {\n\t\t\t\"type\": \"number\"\n\t\t},\n\t\t\"writable\": false,\n\t\t\"hrefs\": [\"temp\"]\n\t}]\n}";
    	
   		// @SuppressWarnings("unused")
		Thing td = ThingDescriptionParser.fromBytes(json.getBytes());
   		assertTrue("No Property temperature", td.getProperty("temperature") != null);
   		Property p = td.getProperty("temperature");
   		assertTrue("No valueType", p.getValueType() != null);
		JsonNode valueType = p.getValueType();
		assertTrue(valueType.findValue("type").asText().equals("number"));
    }
    
    @Test
    // CP: Example 3: More Capabilities
    public void testJSONSchema3() throws Exception {
    	String json = "{\n  \"@context\": [\n    \"http://w3c.github.io/wot/w3c-wot-td-context.jsonld\",\n    { \"actuator\": \"http://example.org/actuator#\" }\n  ],\n  \"@type\": \"Thing\",\n  \"name\": \"MyLEDThing\",\n  \"uris\": [\n    \"coap://myled.example.com:5683/\",\n    \"http://mything.example.com:8080/myled/\"\n  ],\n  \"encodings\": [ \"JSON\",\"EXI\"],\n  \"security\": {\n    \"cat\": \"token:jwt\",\n    \"alg\": \"HS256\",\n    \"as\": \"https://authority-issuing.example.org\"\n  },\n  \"properties\": [\n    {\n      \"@type\": \"actuator:onOffStatus\",\n      \"name\": \"status\",\n      \"valueType\": { \"type\": \"boolean\" },\n      \"writable\": true,\n      \"hrefs\": [ \"pwr\", \"status\" ]\n    }\n  ],\n  \"actions\": [\n    {\n      \"@type\": \"actuator:fadeIn\",\n      \"name\": \"fadeIn\",\n      \"inputData\": {\n        \"valueType\": { \"type\": \"integer\" },\n        \"actuator:unit\": \"actuator:ms\"\n      },\n      \"hrefs\": [\"in\", \"led/in\"  ]\n    },\n    {\n      \"@type\": \"actuator:fadeOut\",\n      \"name\": \"fadeOut\",\n      \"inputData\": {\n        \"valueType\": { \"type\": \"integer\" },\n        \"actuator:unit\": \"actuator:ms\"\n      },\n      \"hrefs\": [\"out\", \"led/out\" ]\n    }\n  ],\n  \"events\": [\n    {\n      \"@type\": \"actuator:alert\",\n      \"name\": \"criticalCondition\",\n      \"valueType\": { \"type\": \"string\" },\n      \"hrefs\": [ \"ev\", \"alert\" ]\n    }\n  ]\n}";
    	
		Thing td = ThingDescriptionParser.fromBytes(json.getBytes());
   		// TODO any further checks?
		
		{
	   		assertTrue("No Property status", td.getProperty("status") != null);
	   		Property p = td.getProperty("status");
	   		assertTrue("No valueType", p.getValueType() != null);
			JsonNode valueType = p.getValueType();
			assertTrue(valueType.findValue("type").asText().equals("boolean"));			
		}

		{
	   		assertTrue("No Action fadeIn", td.getAction("fadeIn") != null);
	   		Action a1 = td.getAction("fadeIn");
	   		assertTrue("No inputType", a1.getInputType() != null);
			JsonNode valueType = a1.getInputType();
			assertTrue(valueType.findValue("type").asText().equals("integer"));
		}
		{
	   		assertTrue("No Action fadeOut", td.getAction("fadeOut") != null);
	   		Action a2 = td.getAction("fadeOut");
	   		assertTrue("No inputType", a2.getInputType() != null);
			JsonNode valueType = a2.getInputType();
			assertTrue(valueType.findValue("type").asText().equals("integer"));
		}
		
		{
	   		assertTrue("No Event criticalCondition", td.getEvent("criticalCondition") != null);
	   		Event e = td.getEvent("criticalCondition");
	   		assertTrue("No valueType", e.getValueType() != null);
			JsonNode valueType = e.getValueType();
			assertTrue(valueType.findValue("type").asText().equals("string"));
		}

		
    }
    
    @Test
    // CP: Example 17: JSON Object
    public void testJSONSchema4() throws Exception {
    	String json = "{\n\t\"@context\": [\"http://w3c.github.io/wot/w3c-wot-td-context.jsonld\"],\n\t\"@type\": \"Thing\",\n\t\"name\": \"MyTestThing\",\n\t\"uris\": \"coap://www.mytest.com:5683/\",\n\t\"encodings\": [\"JSON\"],\n\t\"properties\": [{\n\t\t\"name\": \"obj\",\n\t\t\"valueType\": {\n\t\t\t\"type\": \"object\",\n\t\t\t\"properties\": {\n\t\t\t\t\"id\": {\n\t\t\t\t\t\"type\": \"integer\"\n\t\t\t\t},\n\t\t\t\t\"name\": {\n\t\t\t\t\t\"type\": \"string\"\n\t\t\t\t}\n\t\t\t},\n\t\t\t\"required\": [\"id\"]\n\t\t},\n\t\t\"writable\": false,\n\t\t\"hrefs\": [\"obj\"]\n\t}]\n}";
    	
   		// @SuppressWarnings("unused")
		Thing td = ThingDescriptionParser.fromBytes(json.getBytes());
   		// TODO any further checks?
   		assertTrue("No Property obj", td.getProperty("obj") != null);
   		Property p = td.getProperty("obj");
   		assertTrue("No valueType", p.getValueType() != null);
		JsonNode valueType = p.getValueType();
		assertTrue(valueType.findValue("type").asText().equals("object"));
		assertTrue(valueType.findValue("properties") != null);
		assertTrue(valueType.findValue("required") != null);	
    }
    
    @Test
    // CP: Example 20: JSON Array
    public void testJSONSchema5() throws Exception {
    	String json = "{\n\t\"@context\": [\"http://w3c.github.io/wot/w3c-wot-td-context.jsonld\"],\n\t\"@type\": \"Thing\",\n\t\"name\": \"MyTestThing\",\n\t\"uris\": \"coap://www.mytest.com:5683/\",\n\t\"encodings\": [\"JSON\"],\n\t\"properties\": [{\n\t\t\"name\": \"arr\",\n\t\t\"valueType\": {\n\t\t\t\"type\": \"array\",\n\t\t\t\"items\": {\n\t\t\t\t\"type\": \"number\",\n\t\t\t\t\"minimum\": 0,\n\t\t\t\t\"maximum\": 255\n\t\t\t},\n\t\t\t\"minItems\": 3,\n\t\t\t\"maxItems\": 3\n\t\t},\n\t\t\"writable\": false,\n\t\t\"hrefs\": [\"arr\"]\n\t}]\n}";
    	
   		// @SuppressWarnings("unused")
		Thing td = ThingDescriptionParser.fromBytes(json.getBytes());
   		// TODO any further checks?
   		assertTrue("No Property arr", td.getProperty("arr") != null);
   		Property p = td.getProperty("arr");
   		assertTrue("No valueType", p.getValueType() != null);
		JsonNode valueType = p.getValueType();
		assertTrue(valueType.findValue("type").asText().equals("array"));
		assertTrue(valueType.findValue("items") != null);
		assertTrue(valueType.findValue("minItems").asInt() == 3);
		assertTrue(valueType.findValue("maxItems").asInt() == 3);
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////
    // TESTS for WoT Current Practices (F2F meeting, February 2017, USA, Santa Clara)
    /////////////////////////////////////////////////////////////////////////////////
    
    @Test
    public void testV3_CP_Example1() throws JsonParseException, IOException {
    	String json = "{\r\n" + 
    			"  \"@context\": [\"http://w3c.github.io/wot/w3c-wot-td-context.jsonld\"],\r\n" + 
    			"  \"@type\": \"Thing\",\r\n" + 
    			"  \"name\": \"MyTemperatureThing\",\r\n" + 
    			"  \"interactions\": [\r\n" + 
    			"    {\r\n" + 
    			"      \"@type\": [\"Property\"],\r\n" + 
    			"      \"name\": \"temperature\",\r\n" + 
    			"      \"outputData\": {\"valueType\": { \"type\": \"number\" }},\r\n" + 
    			"      \"writable\": false,\r\n" + 
    			"      \"links\": [{\r\n" + 
    			"        \"href\" : \"coap://mytemp.example.com:5683/temp\",\r\n" + 
    			"        \"mediaType\": \"application/json\"\r\n" + 
    			"        }]\r\n" + 
    			"    }\r\n" + 
    			"  ]\r\n" + 
    			"}";
    	
    	Thing td = ThingDescriptionParser.fromBytes(json.getBytes());
    	
    	// name
    	assertTrue("MyTemperatureThing".equals(td.getName()));
    	
    	// metadata
    	Metadata md = td.getMetadata();
    	assertTrue(md.contains("@context"));
    	assertTrue(md.get("@context").isArray());
    	assertTrue(ThingDescriptionParser.stringOrArray(md.get("@context")).contains("http://w3c.github.io/wot/w3c-wot-td-context.jsonld"));
    	assertTrue(md.contains("@type"));
    	assertTrue(md.get("@type").textValue().equals("Thing"));
    	assertTrue(ThingDescriptionParser.stringOrArray(md.get("encodings")).contains("application/json"));
    	
    	// Interactions
    	Property p = td.getProperty("temperature");
    	List<Property> props = td.getProperties();
    	assertTrue(props.size() == 1);
    	assertTrue(p != null);
    	assertTrue(p == props.get(0));
    	
    	// Property temperature
    	assertTrue("temperature".equals(p.getName()));
    	JsonNode jnValueType = p.getValueType();
    	assertTrue(jnValueType.has("type"));
    	assertTrue("number".equals(jnValueType.get("type").asText()));
    	assertTrue(!p.isWritable());
    	assertTrue(p.getHrefs().size() == 1);
    	assertTrue("coap://mytemp.example.com:5683/temp".equals(p.getHrefs().get(0)));
    }
    
    
    @Test
    public void testV3_CP_Example2() throws JsonParseException, IOException {
    	String json = "{\r\n" + 
    			"\"@context\": [\"http://w3c.github.io/wot/w3c-wot-td-context.jsonld\",\r\n" + 
    			"       { \"sensor\": \"http://example.org/sensors#\" }\r\n" + 
    			"      ],\r\n" + 
    			"  \"@type\": \"Thing\",\r\n" + 
    			"  \"name\": \"MyTemperatureThing\",\r\n" + 
    			"  \"interactions\": [\r\n" + 
    			"    {\r\n" + 
    			"      \"@type\": [\"Property\",\"sensor:Temperature\"\r\n" + 
    			"],\r\n" + 
    			"      \"name\": \"temperature\",\r\n" + 
    			"      \"sensor:unit\": \"sensor:Celsius\",\r\n" + 
    			"\r\n" + 
    			"      \"outputData\": {\"valueType\": { \"type\": \"number\" }},\r\n" + 
    			"      \"writable\": false,\r\n" + 
    			"      \"links\": [{\r\n" + 
    			"        \"href\" : \"coap://mytemp.example.com:5683/\",\r\n" + 
    			"        \"mediaType\": \"application/json\"\r\n" + 
    			"        }]\r\n" + 
    			"    }\r\n" + 
    			"  ]\r\n" + 
    			"}";
    	
    	Thing td = ThingDescriptionParser.fromBytes(json.getBytes());
    	
    	// name
    	assertTrue("MyTemperatureThing".equals(td.getName()));
    	
    	// metadata
    	Metadata md = td.getMetadata();
    	assertTrue(md.contains("@context"));
    	assertTrue(md.get("@context").isArray());
    	assertTrue(ThingDescriptionParser.stringOrArray(md.get("@context")).contains("http://w3c.github.io/wot/w3c-wot-td-context.jsonld"));
    	// TODO @context { "sensor": "http://example.org/sensors#" }
    	assertTrue(md.contains("@type"));
    	assertTrue(md.get("@type").textValue().equals("Thing"));
    	assertTrue(ThingDescriptionParser.stringOrArray(md.get("encodings")).contains("application/json"));
    	
    	// Interactions
    	Property p = td.getProperty("temperature");
    	List<Property> props = td.getProperties();
    	assertTrue(props.size() == 1);
    	assertTrue(p != null);
    	assertTrue(p == props.get(0));
    	
    	// Property temperature
    	assertTrue("temperature".equals(p.getName()));
    	// TODO "sensor:unit": "sensor:Celsius",
    	JsonNode jnValueType = p.getValueType();
    	assertTrue(jnValueType.has("type"));
    	assertTrue("number".equals(jnValueType.get("type").asText()));
    	assertTrue(!p.isWritable());
    	assertTrue(p.getHrefs().size() == 1);
    	assertTrue("coap://mytemp.example.com:5683/".equals(p.getHrefs().get(0)));
    }
    
    
    @Test
    public void testV3_CP_Example3() throws JsonParseException, IOException {
    	String json = "{\r\n" + 
    			"							\"@context\": [\r\n" + 
    			"								\"http://w3c.github.io/wot/w3c-wot-td-context.jsonld\",\r\n" + 
    			"								{ \"actuator\": \"http://example.org/actuator#\" }\r\n" + 
    			"							],\r\n" + 
    			"							\"@type\": \"Thing\",\r\n" + 
    			"							\"name\": \"MyLEDThing\",\r\n" + 
    			"							\"base\": \"coap://myled.example.com:5683/\",\r\n" + 
    			"							\"security\": {\r\n" + 
    			"									\"cat\": \"token:jwt\",\r\n" + 
    			"									\"alg\": \"HS256\",\r\n" + 
    			"									\"as\": \"https://authority-issuing.example.org\"\r\n" + 
    			"								},\r\n" + 
    			"							\"interactions\": [\r\n" + 
    			"								{\r\n" + 
    			"									\"@type\": [\"Property\",\"actuator:onOffStatus\"],\r\n" + 
    			"									\"name\": \"status\",\r\n" + 
    			"									\"outputData\": {\"valueType\": { \"type\": \"boolean\" }},\r\n" + 
    			"									\"writable\": true,\r\n" + 
    			"									\"links\": [{\r\n" + 
    			"										\"href\" : \"pwr\", \r\n" + 
    			"										\"mediaType\": \"application/exi\" \r\n" + 
    			"									},\r\n" + 
    			"									{\r\n" + 
    			"										\"href\" : \"http://mytemp.example.com:8080/status\",\r\n" + 
    			"										\"mediaType\": \"application/json\"\r\n" + 
    			"									}]\r\n" + 
    			"								},\r\n" + 
    			"								{\r\n" + 
    			"									\"@type\": [\"Action\",\"actuator:fadeIn\"],\r\n" + 
    			"									\"name\": \"fadeIn\",\r\n" + 
    			"									\"inputData\": {\r\n" + 
    			"										\"valueType\": { \"type\": \"integer\" },\r\n" + 
    			"										\"actuator:unit\": \"actuator:ms\"\r\n" + 
    			"									},\r\n" + 
    			"									\"links\": [{\r\n" + 
    			"										\"href\" : \"in\", \r\n" + 
    			"										\"mediaType\": \"application/exi\" \r\n" + 
    			"									},\r\n" + 
    			"									{\r\n" + 
    			"										\"href\" : \"http://mytemp.example.com:8080/in\",\r\n" + 
    			"										\"mediaType\": \"application/json\"\r\n" + 
    			"									}]									\r\n" + 
    			"								},\r\n" + 
    			"								{\r\n" + 
    			"									\"@type\": [\"Action\",\"actuator:fadeOut\"],\r\n" + 
    			"									\"name\": \"fadeOut\",\r\n" + 
    			"									\"inputData\": {\r\n" + 
    			"										\"valueType\": { \"type\": \"integer\" },\r\n" + 
    			"										\"actuator:unit\": \"actuator:ms\"\r\n" + 
    			"									},\r\n" + 
    			"									\"links\": [{\r\n" + 
    			"										\"href\" : \"out\", \r\n" + 
    			"										\"mediaType\": \"application/exi\" \r\n" + 
    			"									},\r\n" + 
    			"									{\r\n" + 
    			"										\"href\" : \"http://mytemp.example.com:8080/out\",\r\n" + 
    			"										\"mediaType\": \"application/json\"\r\n" + 
    			"									}]									\r\n" + 
    			"								},\r\n" + 
    			"								{\r\n" + 
    			"									\"@type\": [\"Event\",\"actuator:alert\"],\r\n" + 
    			"									\"name\": \"criticalCondition\",\r\n" + 
    			"									\"outputData\": {\"valueType\": { \"type\": \"string\" }},\r\n" + 
    			"									\"links\": [{\r\n" + 
    			"						              \"href\" : \"ev\",\r\n" + 
    			"						              \"mediaType\": \"application/exi\"\r\n" + 
    			"						            }]	\r\n" + 
    			"								}\r\n" + 
    			"							]\r\n" + 
    			"						}";
    	
    	Thing td = ThingDescriptionParser.fromBytes(json.getBytes());
    	
    	// name
    	assertTrue("MyLEDThing".equals(td.getName()));
    	
    	// metadata
    	Metadata md = td.getMetadata();
    	assertTrue(md.contains("@context"));
    	assertTrue(md.get("@context").isArray());
    	assertTrue(ThingDescriptionParser.stringOrArray(md.get("@context")).contains("http://w3c.github.io/wot/w3c-wot-td-context.jsonld"));
    	// TODO @context { "actuator": "http://example.org/actuator#" }
    	assertTrue(md.contains("@type"));
    	assertTrue(md.get("@type").textValue().equals("Thing"));
    	// base --> uris
    	assertTrue(md.get("uris").textValue().equals("coap://myled.example.com:5683/"));
    	assertTrue(ThingDescriptionParser.stringOrArray(md.get("encodings")).contains("application/json"));
    	assertTrue(ThingDescriptionParser.stringOrArray(md.get("encodings")).contains("application/exi"));
    	assertTrue(md.contains("security"));
    	JsonNode jnSecurity = md.get("security");
    	assertTrue("token:jwt".equals(jnSecurity.get("cat").asText()));
    	assertTrue("HS256".equals(jnSecurity.get("alg").asText()));
    	assertTrue("https://authority-issuing.example.org".equals(jnSecurity.get("as").asText()));
    	
    	// Interactions Properties
    	Property p = td.getProperty("status");
    	List<Property> props = td.getProperties();
    	assertTrue(props.size() == 1);
    	assertTrue(p != null);
    	assertTrue(p == props.get(0));
    	
    	// Property status
    	{
        	assertTrue("status".equals(p.getName()));
        	JsonNode jnValueType = p.getValueType();
        	assertTrue(jnValueType.has("type"));
        	assertTrue("boolean".equals(jnValueType.get("type").asText()));
        	assertTrue(p.isWritable());
        	assertTrue(p.getHrefs().size() == 2);
        	assertTrue(p.getHrefs().contains("pwr"));
        	assertTrue(p.getHrefs().contains("http://mytemp.example.com:8080/status"));    		
    	}

    	// Interactions Actions
    	Action a1 = td.getAction("fadeIn");
    	Action a2 = td.getAction("fadeOut");
    	List<Action> actions = td.getActions();
    	assertTrue(actions.size() == 2);
    	assertTrue(a1 != null);
    	assertTrue(a2 != null);
    	assertTrue(actions.contains(a1));
    	assertTrue(actions.contains(a2));
    	// Action fadeIn
    	{
    		assertTrue("fadeIn".equals(a1.getName()));
    		
        	JsonNode jnInputType = a1.getInputType();
        	assertTrue(jnInputType.has("type"));
        	assertTrue("integer".equals(jnInputType.get("type").asText()));
        	assertTrue(a1.getHrefs().size() == 2);
        	assertTrue(a1.getHrefs().contains("in"));
        	assertTrue(a1.getHrefs().contains("http://mytemp.example.com:8080/in"));
        	
        	assertTrue(a1.getOutputType() == null);
        	assertTrue(a1.getSecurity() == null);
    	}
    	// Action fadeOut
    	{
    		assertTrue("fadeOut".equals(a2.getName()));
    		
        	JsonNode jnInputType = a2.getInputType();
        	assertTrue(jnInputType.has("type"));
        	assertTrue("integer".equals(jnInputType.get("type").asText()));
        	assertTrue(a2.getHrefs().size() == 2);
        	assertTrue(a2.getHrefs().contains("out"));
        	assertTrue(a2.getHrefs().contains("http://mytemp.example.com:8080/out"));
        	
        	assertTrue(a2.getOutputType() == null);
        	assertTrue(a2.getSecurity() == null);
    	}
    	
    	
    	// Interactions Events
    	Event e = td.getEvent("criticalCondition");
    	List<Event> events = td.getEvents();
    	assertTrue(events.size() == 1);
    	assertTrue(e != null);
    	assertTrue(events.contains(e));
    	// Event criticalCondition
    	{
    		assertTrue("criticalCondition".equals(e.getName()));
    		
        	JsonNode jnOutputType = e.getValueType();
        	assertTrue(jnOutputType.has("type"));
        	assertTrue("string".equals(jnOutputType.get("type").asText()));
        	assertTrue(e.getHrefs().size() == 1);
        	assertTrue(e.getHrefs().contains("ev"));
        	
        	assertTrue(a2.getSecurity() == null);
    	}
    	
    }
    
    
    @Test
    public void test_ExampleType() throws JsonParseException, IOException {
    	String json = "{\r\n" + 
    			"	\"@context\": [\r\n" + 
    			"		\"http://w3c.github.io/wot/w3c-wot-td-context.jsonld\"\r\n" + 
    			"	],\r\n" + 
    			"	\"@type\": \"Thing\",\r\n" + 
    			"	\"name\": \"MyLatitude\",\r\n" + 
    			"	\"base\": \"coap://myled.example.com:5683/\",\r\n" + 
    			"	\"interactions\": [{\r\n" + 
    			"		\"@type\": [\r\n" + 
    			"			\"Property\",\r\n" + 
    			"			\"schema:latitude\"\r\n" + 
    			"		],\r\n" + 
    			"		\"name\": \"latitude\",\r\n" + 
    			"		\"outputData\": {\r\n" + 
    			"			\"valueType\": {\r\n" + 
    			"				\"type\": \"schema:latitude\"\r\n" + 
    			"			}\r\n" + 
    			"		},\r\n" + 
    			"		\"writable\": true,\r\n" + 
    			"		\"links\": [{\r\n" + 
    			"			\"href\": \"latitude\",\r\n" + 
    			"			\"mediaType\": \"application/json\"\r\n" + 
    			"		}]\r\n" + 
    			"	}]\r\n" + 
    			"}";
    	
    	Thing td = ThingDescriptionParser.fromBytes(json.getBytes());
    	
    	// name
    	assertTrue("MyLatitude".equals(td.getName()));
    	
    	// name property
    	assertTrue("latitude".equals(td.getProperty("latitude").getName()));
    	assertTrue("schema:latitude".equals(td.getProperty("latitude").getPropertyType()));
    }

}
