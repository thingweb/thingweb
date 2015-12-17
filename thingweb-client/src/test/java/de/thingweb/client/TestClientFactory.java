package de.thingweb.client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;

import de.thingweb.client.Client;
import de.thingweb.client.ClientFactory;
import de.thingweb.client.UnsupportedException;
import junit.framework.TestCase;

public class TestClientFactory extends TestCase {

	@Test
	public void testUrlTutorialDoor() throws JsonParseException, IOException, UnsupportedException, URISyntaxException {
		URL jsonld = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/door.jsonld");
		
		ClientFactory cf = new ClientFactory();
		@SuppressWarnings("unused")
		Client client = cf.getClientUrl(jsonld);
	}

}
