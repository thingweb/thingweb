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

package de.thingweb.client;

import com.fasterxml.jackson.core.JsonParseException;

import de.thingweb.client.impl.CoapClientImpl;
import de.thingweb.client.impl.HttpClientImpl;
import de.thingweb.desc.ThingDescriptionParser;
import de.thingweb.thing.Thing;

import org.eclipse.californium.core.CoapClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class ClientFactory {
	
	private static final Logger log = LoggerFactory.getLogger(ClientFactory.class);
	
	Thing thing;
	
	boolean isCoapScheme(String scheme) {
		return("coap".equals(scheme) || "coaps".equals(scheme));
	}
	
	boolean isHttpScheme(String scheme) {
		return("http".equals(scheme) || "https".equals(scheme));
	}

	public Client getClientUrl(URI jsonld) throws JsonParseException, IOException, UnsupportedException, URISyntaxException {
		// URL can't handle coap uris --> use URI only
		if(isCoapScheme(jsonld.getScheme())) {
			CoapClient coap = new CoapClient(jsonld);
			
			// synchronous coap
			byte[] content = coap.get().getPayload();
			thing = ThingDescriptionParser.fromBytes(content);
			
			return getClient();
		} else {
			return getClientUrl(jsonld.toURL());
		}	
	}
	
	protected Client getClient() throws UnsupportedException, URISyntaxException {
		assert(thing != null);
		
		processThingDescription();
		
		// pick the right client
		return pickClient();
	}
	
	public Client getClientUrl(URL jsonld) throws JsonParseException, IOException, UnsupportedException, URISyntaxException {
		// Note: URL can't handle coap --> needs to be done before
		thing = ThingDescriptionParser.fromURL(jsonld);
		return getClient();
	}
	
	public Client getClientFile(String jsonld) throws FileNotFoundException, IOException, UnsupportedException, URISyntaxException {
		thing = ThingDescriptionParser.fromFile(jsonld);
		return getClient();
	}
	
	public Client getClientFromTD(Thing thing) throws UnsupportedException, URISyntaxException {
		this.thing = thing;
		return getClient();
	}

	protected void processThingDescription() {
		// TODO if anything is wrong or inconsistent with thingweb-repository, put glue code here...
	}

	protected Client pickClient() throws UnsupportedException, URISyntaxException {
		// check for right protocol&encoding
		List<Client> clients = new ArrayList<>(); // it is assumed URIs are ordered by priority
		
		List<String> uris = thing.getMetadata().getAll("uris");
    if (uris != null) {
      int prio = 1;
      for (String suri : uris) {
        URI uri = new URI(suri);
        if(isCoapScheme(uri.getScheme())) {
          Client c = new CoapClientImpl(suri, thing);
          log.info("Found matching client '" + CoapClientImpl.class.getName() + "' with priority " + prio++);
        } else if(isHttpScheme(uri.getScheme())) {
          Client c = new HttpClientImpl(suri, thing);
          log.info("Found matching client '" + HttpClientImpl.class.getName() + "' with priority " + prio++);
        } 
      }
    }
		
		// take priority into account
		if(clients.isEmpty()) {
			log.warn("No fitting client implementation found!");
			throw new UnsupportedException("No fitting client implementation found!");
			// return null;
		} else {
			// pick first one with highest priority
			Client c = clients.get(0);
			log.info("Use '" + c.getClass().getName() + "' according to priority");
			return c;
		}


	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException, UnsupportedException, URISyntaxException {

//		// led (local)
//		String jsonld = "jsonld" + File.separator + "led.jsonld";
//		// led (URL)
//		URL jsonld = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/led.jsonld");
		// door
		 URL jsonld = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/door.jsonld");
		
		ClientFactory cf = new ClientFactory();
		@SuppressWarnings("unused")
		Client client = cf.getClientUrl(jsonld);
		
	}
}
