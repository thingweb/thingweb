package de.thingweb.client;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thingweb.client.impl.CoapClientImpl;
import de.thingweb.client.impl.HttpClientImpl;
import de.thingweb.thing.*;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class TestClientFactory extends TestCase {

	@Test
	public void testUrlTutorialDoor_OldTD() throws JsonParseException, IOException, UnsupportedException, URISyntaxException {
		URL jsonld = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/door.jsonld");
		
		ClientFactory cf = new ClientFactory();
		Client client = cf.getClientUrl(jsonld);
		assertTrue(client instanceof HttpClientImpl);
	}
	
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
		pout.write("		\"href\": \"stateOpen\"");
		pout.write("	}");
		pout.write("  ],");
		pout.write("  \"events\": [");
		pout.write("    {");
		pout.write("      \"name\": \"stateChanged\",");
		pout.write("	  \"valueType\": \"xsd:boolean\",");
		pout.write("      \"href\": [ \"ev\", \"myled/event\" ]");
		pout.write("    }");
		pout.write("  ]");
		pout.write("}");
		pout.close();
		
		ClientFactory cf = new ClientFactory();
		Client client = cf.getClientFile(f.getAbsolutePath());
		System.out.println(client);
		
		assertTrue(client instanceof HttpClientImpl);
		assertTrue("http://www.example.com:80/door".equals(client.getUsedProtocolURI()));
		assertTrue("MyDoor".equals(client.getThing().getName()));
		// actions
		assertTrue(client.getThing().getActions() == null || client.getThing().getActions().isEmpty());
		// properties
		assertTrue(!client.getThing().getProperties().isEmpty());
		assertTrue(client.getThing().getProperties().get(0).getName().equals("stateOpen"));
		assertTrue(client.getThing().getProperties().get(0).getValueType().asText().equals("xsd:boolean"));
		// events
		assertTrue(!client.getThing().getEvents().isEmpty());
		assertTrue(client.getThing().getEvents().get(0).getName().equals("stateChanged"));
		assertTrue(client.getThing().getEvents().get(0).getValueType().asText().equals("xsd:boolean"));
		
		// TODO add more tests such as events, properties
		
	}
	
	
	@Test
	// CP Document: Example 3: More Capabilities
	public void testJSONSchema3_More_Capabilities() throws JsonParseException, IOException, UnsupportedException, URISyntaxException {
		String json = "{\n  \"@context\": [\n    \"http://w3c.github.io/wot/w3c-wot-td-context.jsonld\",\n    { \"actuator\": \"http://example.org/actuator#\" }\n  ],\n  \"@type\": \"Thing\",\n  \"name\": \"MyLEDThing\",\n  \"uris\": [\n    \"coap://myled.example.com:5683/\",\n    \"http://mything.example.com:8080/myled/\"\n  ],\n  \"encodings\": [ \"JSON\",\"EXI\"],\n  \"security\": {\n    \"cat\": \"token:jwt\",\n    \"alg\": \"HS256\",\n    \"as\": \"https://authority-issuing.example.org\"\n  },\n  \"properties\": [\n    {\n      \"@type\": \"actuator:onOffStatus\",\n      \"name\": \"status\",\n      \"valueType\": { \"type\": \"boolean\" },\n      \"writable\": true,\n      \"hrefs\": [ \"pwr\", \"status\" ]\n    }\n  ],\n  \"actions\": [\n    {\n      \"@type\": \"actuator:fadeIn\",\n      \"name\": \"fadeIn\",\n      \"inputData\": {\n        \"valueType\": { \"type\": \"integer\" },\n        \"actuator:unit\": \"actuator:ms\"\n      },\n      \"hrefs\": [\"in\", \"led/in\"  ]\n    },\n    {\n      \"@type\": \"actuator:fadeOut\",\n      \"name\": \"fadeOut\",\n      \"inputData\": {\n        \"valueType\": { \"type\": \"integer\" },\n        \"actuator:unit\": \"actuator:ms\"\n      },\n      \"hrefs\": [\"out\", \"led/out\" ]\n    }\n  ],\n  \"events\": [\n    {\n      \"@type\": \"actuator:alert\",\n      \"name\": \"criticalCondition\",\n      \"valueType\": { \"type\": \"string\" },\n      \"hrefs\": [ \"ev\", \"alert\" ]\n    }\n  ]\n}";
    	File f = File.createTempFile("jsonTest3", "jsonld");
    	BufferedWriter out = new BufferedWriter(new FileWriter(f));
        out.write(json);
        out.close();
		
		ClientFactory cf = new ClientFactory();
		Client client = cf.getClientFile(f.getAbsolutePath());
		System.out.println(client);
		
		assertTrue(client instanceof HttpClientImpl || client instanceof CoapClientImpl);
		if(client instanceof HttpClientImpl) {
			assertTrue("http://mything.example.com:8080/myled/".equals(client.getUsedProtocolURI()));
		} else {
			assertTrue("coap://myled.example.com:5683/".equals(client.getUsedProtocolURI()));
		}
		
		Thing t = client.getThing();
		assertTrue("MyLEDThing".equals(t.getName()));
		Metadata md = t.getMetadata();
		
		// encodings
		List<String> encs = md.getAll("encodings");
		assertTrue(encs.equals(Arrays.asList("JSON", "EXI")));
		// String enc = md.get("encodings");
		
		// security
		String sec = md.get("security");
		assertTrue(sec != null);
		{
			ObjectMapper mapper = new ObjectMapper();
			JsonNode valueType = mapper.readValue(new StringReader(sec), JsonNode.class);
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
		}
		{
			assertTrue(t.getAction("fadeOut") != null);
			Action a = t.getAction("fadeOut");
			assertTrue(a.getName().equals("fadeOut"));
			assertTrue(a.getActionType().equals("actuator:fadeOut"));
			assertTrue(a.getInputType() != null);
			assertTrue(a.getHrefs().equals(Arrays.asList("out", "led/out")));
			assertTrue(a.getSecurity() == null || a.getSecurity().equals(""));	
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
			assertTrue("http://mything.example.com:8080/temperature/".equals(client.getUsedProtocolURI()));
		} else {
			assertTrue("coap://myled.example.com:5683/".equals(client.getUsedProtocolURI()));
		}
		
		Thing t = client.getThing();
		assertTrue("MyLEDThing".equals(t.getName()));
		Metadata md = t.getMetadata();
		
		// encodings
		List<String> encs = md.getAll("encodings");
		assertTrue(encs.equals(Arrays.asList("JSON", "EXI")));
		// String enc = md.get("encodings");
		
		// security
		String sec = md.get("security");
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
