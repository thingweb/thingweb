package de.thingweb.client;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;

import de.thingweb.client.Client;
import de.thingweb.client.ClientFactory;
import de.thingweb.client.UnsupportedException;
import de.thingweb.client.impl.HttpClientImpl;
import junit.framework.TestCase;

public class TestClientFactory extends TestCase {

	@Test
	public void testUrlTutorialDoor_OldTD() throws JsonParseException, IOException, UnsupportedException, URISyntaxException {
		URL jsonld = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/door.jsonld");
		
		ClientFactory cf = new ClientFactory();
		Client client = cf.getClientUrl(jsonld);
		assertTrue(client instanceof HttpClientImpl);
	}
	
	@Test
	public void testUrlTutorialDoor() throws JsonParseException, IOException, UnsupportedException, URISyntaxException {
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
		assertTrue(client.getUsedProtocolURI().equals("http://www.example.com:80/door"));
		// TODO add more tests such as events, properties
		
	}

}
