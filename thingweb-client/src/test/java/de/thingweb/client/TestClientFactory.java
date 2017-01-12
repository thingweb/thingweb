package de.thingweb.client;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import de.thingweb.client.impl.CoapClientImpl;
import de.thingweb.client.impl.HttpClientImpl;
import de.thingweb.thing.*;
import junit.framework.TestCase;

import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class TestClientFactory extends TestCase {

	private static final JsonNodeFactory factory = new JsonNodeFactory(false);
	
	// Note: some test-cases are ignored given that the online resource (URL) does not exist anymore
	
//	@Ignore @Test
//	public void testUrlTutorialDoor_OldTD() throws JsonParseException, IOException, UnsupportedException, URISyntaxException {
//		URL jsonld = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/door.jsonld");
//		
//		ClientFactory cf = new ClientFactory();
//		Client client = cf.getClientUrl(jsonld);
//		assertTrue(client instanceof HttpClientImpl);
//	}
	
	@Test
	public void testFileTutorialDoor() throws JsonParseException, IOException, UnsupportedException, URISyntaxException {
		File f = File.createTempFile("door", "jsonld");
		PrintWriter pout = new PrintWriter(f);
		pout.write("{");
		pout.write("  \"@context\": \"http://w3c.github.io/wot/w3c-wot-td-context.jsonld\",");
		pout.write("  \"@type\": \"Thing\",");
		pout.write("  \"name\": \"MyDoor\",");
		pout.write("  \"uris\" : \"http://www.example.com:80/door\",");
		pout.write("  \"encodings\": [\"JSON\"],");
		pout.write("  \"properties\": [");
		pout.write("	{");
		pout.write("		\"name\": \"stateOpen\",");
		pout.write("		\"valueType\": \"xsd:boolean\",");
		pout.write("		\"hrefs\": \"stateOpen\"");
		pout.write("	}");
		pout.write("  ],");
		pout.write("  \"events\": [");
		pout.write("    {");
		pout.write("      \"name\": \"stateChanged\",");
		pout.write("	  \"valueType\": \"xsd:boolean\",");
		pout.write("      \"hrefs\": [ \"ev\", \"myled/event\" ]");
		pout.write("    }");
		pout.write("  ]");
		pout.write("}");
		pout.close();
		
		ClientFactory cf = new ClientFactory();
		Client client = cf.getClientFile(f.getAbsolutePath());
		System.out.println(client);
		
		assertTrue(client instanceof HttpClientImpl);
		assertTrue(new URI("http://www.example.com:80/door").equals(client.getThing().getUri(0)));
		assertTrue("MyDoor".equals(client.getThing().getName()));
		// actions
		assertTrue(client.getThing().getActions() == null || client.getThing().getActions().isEmpty());
		// properties
		assertTrue(!client.getThing().getProperties().isEmpty());
		assertTrue(client.getThing().getProperties().get(0).getName().equals("stateOpen"));
		assertTrue(client.getThing().getProperties().get(0).getValueType().asText().equals("xsd:boolean"));
		assertTrue(client.getThing().getProperties().get(0).getHrefs().size() == 1);
		assertTrue("http://www.example.com:80/door/stateOpen".equals(client.getThing().resolvePropertyUri("stateOpen", 0)));
		// events
		assertTrue(!client.getThing().getEvents().isEmpty());
		assertTrue(client.getThing().getEvents().get(0).getName().equals("stateChanged"));
		assertTrue(client.getThing().getEvents().get(0).getValueType().asText().equals("xsd:boolean"));
		assertTrue(client.getThing().getEvents().get(0).getHrefs().size() == 2);
		
		// TODO add more tests such as events, properties
		
		
	}
	
	
	@Test
	// CP Document: Example 3: More Capabilities
	public void testJSONSchema3_More_Capabilities() throws JsonParseException, IOException, UnsupportedException, URISyntaxException {
		String json = "{\r\n" + 
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
				"        \"valueType\": { \"type\": \"integer\" },\r\n" + 
				"        \"actuator:unit\": \"actuator:ms\"\r\n" + 
				"      },\r\n" + 
				"      \"hrefs\": [\"in\", \"led/in\"  ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"@type\": \"actuator:fadeOut\",\r\n" + 
				"      \"name\": \"fadeOut\",\r\n" + 
				"      \"inputData\": {\r\n" + 
				"        \"valueType\": { \"type\": \"integer\" },\r\n" + 
				"        \"actuator:unit\": \"actuator:ms\"\r\n" + 
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
    	File f = File.createTempFile("jsonTest3", "jsonld");
    	BufferedWriter out = new BufferedWriter(new FileWriter(f));
        out.write(json);
        out.close();
		
		ClientFactory cf = new ClientFactory();
		Client client = cf.getClientFile(f.getAbsolutePath());
		System.out.println(client);
		
		String coapBaseUri = "coap://myled.example.com:5683/";
		String httpBaseUri = "http://mything.example.com:8080/myled/";
		
		assertTrue(client instanceof HttpClientImpl || client instanceof CoapClientImpl);
		if(client instanceof HttpClientImpl) {
			assertTrue(new URI(httpBaseUri).equals(client.getThing().getUri(1)));
		} else {
			assertTrue(new URI(coapBaseUri).equals(client.getThing().getUri(0)));
		}
		
		Thing t = client.getThing();
		assertTrue("MyLEDThing".equals(t.getName()));
		Metadata md = t.getMetadata();
		
		// encodings
		ArrayNode an = factory.arrayNode();
		an.add("JSON");
		an.add("EXI");
		JsonNode encs = md.get("encodings");
		assertTrue(encs.equals(an));
		// String enc = md.get("encodings");
		
		// security
		JsonNode sec = md.get("security");
		assertTrue(sec != null);
		{
			JsonNode valueType = sec;
			assertTrue(valueType.findValue("cat").asText().equals("token:jwt"));
			assertTrue(valueType.findValue("alg").asText().equals("HS256"));
			assertTrue(valueType.findValue("as").asText().equals("https://authority-issuing.example.org"));
		}

		// properties
		{
			assertTrue(!client.getThing().getProperties().isEmpty());
			assertTrue(t.getProperty("status") != null);
			Property p = t.getProperty("status");
			assertTrue(p.getName().equals("status"));
			assertTrue(p.getPropertyType().equals("actuator:onOffStatus"));
			assertTrue(p.getValueType().toString().contains("boolean"));
			assertTrue(p.isWritable() == true);
			assertTrue(p.getHrefs().equals(Arrays.asList("pwr", "status")));
			assertTrue(p.getSecurity() == null || p.getSecurity().equals(""));		
			assertTrue(p.getHrefs().size() == 2);
			assertTrue((coapBaseUri + "pwr").equals(client.getThing().resolvePropertyUri("status", 0)));
			assertTrue((httpBaseUri + "status").equals(client.getThing().resolvePropertyUri("status", 1)));
		}

		
		// actions
		assertTrue(!client.getThing().getActions().isEmpty());
		{
			assertTrue(t.getAction("fadeIn") != null);
			Action a = t.getAction("fadeIn");
			assertTrue(a.getName().equals("fadeIn"));
			assertTrue(a.getActionType().equals("actuator:fadeIn"));
			assertTrue(a.getInputType() != null);
			assertTrue(a.getHrefs().equals(Arrays.asList("in", "led/in")));
			assertTrue(a.getSecurity() == null || a.getSecurity().equals(""));	
			assertTrue(a.getHrefs().size() == 2);
			assertTrue((coapBaseUri + "in").equals(client.getThing().resolveActionUri("fadeIn", 0)));
			assertTrue((httpBaseUri + "led/in").equals(client.getThing().resolveActionUri("fadeIn", 1)));
		}
		{
			assertTrue(t.getAction("fadeOut") != null);
			Action a = t.getAction("fadeOut");
			assertTrue(a.getName().equals("fadeOut"));
			assertTrue(a.getActionType().equals("actuator:fadeOut"));
			assertTrue(a.getInputType() != null);
			assertTrue(a.getHrefs().equals(Arrays.asList("out", "led/out")));
			assertTrue(a.getSecurity() == null || a.getSecurity().equals(""));	
			assertTrue(a.getHrefs().size() == 2);
			assertTrue((coapBaseUri + "out").equals(client.getThing().resolveActionUri("fadeOut", 0)));
			assertTrue((httpBaseUri + "led/out").equals(client.getThing().resolveActionUri("fadeOut", 1)));
		}

		// events
		{
			assertTrue(t.getEvent("criticalCondition") != null);
			Event e = t.getEvent("criticalCondition");
			assertTrue(e.getName().equals("criticalCondition"));
			assertTrue(e.getEventType().equals("actuator:alert"));
			assertTrue(e.getValueType() != null);
			assertTrue(e.getHrefs().equals(Arrays.asList("ev", "alert")));
			assertTrue(e.getSecurity() == null || e.getSecurity().equals(""));	
		}
		
		// TODO add more tests
		
	}
	
	
	@Test
	// CP Document: Example 9, inner security, stability, semantic annotations
	public void testJSONSchema4() throws JsonParseException, IOException, UnsupportedException, URISyntaxException {
		String json = "{\n\t\"@context\": [\n\t\t\"http://w3c.github.io/wot/w3c-wot-td-context.jsonld\", {\n\t\t\t\"actuator\": \"http://example.org/actuator#\"\n\t\t}\n\t],\n\t\"@type\": \"Thing\",\n\t\"name\": \"MyLEDThing\",\n\t\"uris\": [\n\t\t\"coap://myled.example.com:5683/\",\n\t\t\"http://mything.example.com:8080/myled/\"\n\t],\n\t\"encodings\": [\"JSON\", \"EXI\"],\n\t\"properties\": [{\n\t\t\"@type\": \"sensor:Temperature\",\n\t\t\"name\": \"temperature\",\n\t\t\"valueType\": {\n\t\t\t\"type\": \"number\"\n\t\t},\n\t\t\"sensor:unit\": \"sensor:Celsius\",\n\t\t\"writable\": false,\n\t\t\"hrefs\": \"temp\",\n\t\t\"stability\": 10,\n\t\t\"security\": {\n\t\t\t\"cat\": \"token:jwt\",\n\t\t\t\"alg\": \"HS256\",\n\t\t\t\"as\": \"https://authority-issuing.example.org\"\n\t\t}\n\t}]\n}";
    	File f = File.createTempFile("jsonTest4", "jsonld");
    	BufferedWriter out = new BufferedWriter(new FileWriter(f));
        out.write(json);
        out.close();
		
		ClientFactory cf = new ClientFactory();
		Client client = cf.getClientFile(f.getAbsolutePath());
		System.out.println(client);
		
		assertTrue(client instanceof HttpClientImpl || client instanceof CoapClientImpl);
		if(client instanceof HttpClientImpl) {
			assertTrue(new URI("http://mything.example.com:8080/temperature/").equals(client.getThing().getUri(1)));
		} else {
			assertTrue(new URI("coap://myled.example.com:5683/").equals(client.getThing().getUri(0)));
		}
		
		Thing t = client.getThing();
		assertTrue("MyLEDThing".equals(t.getName()));
		Metadata md = t.getMetadata();
		
		// encodings
		ArrayNode an = factory.arrayNode();
		an.add("JSON");
		an.add("EXI");
		JsonNode encs = md.get("encodings");
		assertTrue(encs.equals(an));
		// String enc = md.get("encodings");
		
		// security
		JsonNode sec = md.get("security");
		assertTrue(sec == null);

		// properties
		assertTrue(!client.getThing().getProperties().isEmpty());
		{
			assertTrue(t.getProperty("temperature") != null);
			Property p = t.getProperty("temperature");
			assertTrue(p.getName().equals("temperature"));
			assertTrue(p.getPropertyType().equals("sensor:Temperature"));
			assertTrue(p.getValueType().toString().contains("number"));
			assertTrue(p.isWritable() == false);
			assertTrue(p.getStability().equals(10));	
			assertTrue(p.getSecurity() != null || !p.getSecurity().equals(""));	
			{
				JsonNode valueType = p.getSecurity();
				assertTrue(valueType.findValue("cat").asText().equals("token:jwt"));
				assertTrue(valueType.findValue("alg").asText().equals("HS256"));
				assertTrue(valueType.findValue("as").asText().equals("https://authority-issuing.example.org"));
			}
			// TODO "sensor:unit": "sensor:Celsius",
		}

		
		// actions
		assertTrue(client.getThing().getActions().isEmpty());

		// events
		assertTrue(client.getThing().getEvents().isEmpty());
		
		// TODO add more tests
		
	}

}
