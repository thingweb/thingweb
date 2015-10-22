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

package de.webthing.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;

import de.webthing.client.impl.CoapClientImpl;
import de.webthing.client.impl.HttpClientImpl;
import de.webthing.desc.DescriptionParser;
import de.webthing.desc.pojo.ActionDescription;
import de.webthing.desc.pojo.EventDescription;
import de.webthing.desc.pojo.InteractionDescription;
import de.webthing.desc.pojo.Metadata;
import de.webthing.desc.pojo.PropertyDescription;
import de.webthing.desc.pojo.Protocol;
import de.webthing.desc.pojo.ThingDescription;

public class ClientFactory {
	
	private static final Logger log = LoggerFactory.getLogger(ClientFactory.class);
	
	ThingDescription td;
	List<PropertyDescription> properties;
	List<ActionDescription> actions;
	List<EventDescription> events;

	List<String> encodings;
	List<Protocol> protocols;
	
	public Client getClient(URL jsonld) throws JsonParseException, IOException, UnsupportedException {
		td = DescriptionParser.fromURL(jsonld);
		processThingDescription();
		
		// pick the right client
		return pickClient();
	}
	
	public Client getClient(String jsonld) throws FileNotFoundException, IOException, UnsupportedException {
		td = DescriptionParser.fromFile(jsonld);
		processThingDescription();
		
		// pick the right client
		return pickClient();
	}
	
	
	protected void processThingDescription() {
		assert(td != null);
		
		actions = new ArrayList<>();
		properties = new ArrayList<>();
		events = new ArrayList<>();
		encodings = new ArrayList<>();
		protocols = new ArrayList<>();

		log.debug("Process thing dedesription");
		log.debug("# Interactions");
		List<InteractionDescription> interactions = td.getInteractions();
		for(InteractionDescription id : interactions) {
			String iname = id.getName();
			log.debug("InteractionDescription name: " + iname);
			if(id instanceof ActionDescription) {
				ActionDescription ad = (ActionDescription) id;
				log.debug("\tinput:  " + ad.getInputType());
				log.debug("\toutput: " + ad.getOutputType());
				actions.add(ad);
			} else if(id instanceof PropertyDescription) {
				PropertyDescription pd = (PropertyDescription) id;
				log.debug("\toutput: " + pd.getOutputType());
				log.debug("\twritable: " + pd.isWritable());
				properties.add(pd);
			} else if(id instanceof EventDescription) {
				EventDescription ed = (EventDescription) id;
				log.debug("\toutput: " + ed.getOutputType());
				events.add(ed);
			} else {
				log.warn("Unexpected interaction type: " + id);
			}
		}
		
		Metadata metadata = td.getMetadata();
		log.debug("# Metadata " + metadata.getName());
		log.debug("# Encodings");
		List<String> encs = metadata.getEncodings();
		for(String enc : encs) {
			log.debug(enc);
			encodings.add(enc);
		}
		log.debug("# Encodings");
		Map<String,Protocol> prots = metadata.getProtocols();
		for(String ps : prots.keySet()) {
			log.debug(ps);
			Protocol p = prots.get(ps);
			protocols.add(p);
			log.debug("\t" + p.getUri());
		}
	}

	protected Client pickClient() throws UnsupportedException {
		// check for right protocol&encoding
		TreeMap<Integer, Client> tm = new TreeMap<>(); // sorted according priority
		
		
		for(Protocol p : protocols) {
			if(p.getUri().startsWith("coap:")) {
				Client c = new CoapClientImpl(p, properties, actions, events);
				tm.put(p.priority, c);
				log.info("Found matching client '" + CoapClientImpl.class.getName() + "' with priority " + p.priority);
			} else if(p.getUri().startsWith("http:")) {
				Client c = new HttpClientImpl(p, properties, actions, events);
				tm.put(p.priority, c);
				log.info("Found matching client '" + HttpClientImpl.class.getName() + "' with priority " + p.priority);
			}
		}
		
		// take priority into account
		Set<Integer> keys = tm.keySet();
		if(keys.isEmpty()) {
			log.warn("No fitting client implementation found!");
			throw new UnsupportedException("No fitting client implementation found!");
			// return null;
		} else {
			// pick first one with highest priority
			Client c = tm.get(keys.iterator().next());
			log.info("Use '" + c.getClass().getName() + "' according to priority");
			return c;
		}


	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException, UnsupportedException {

//		// led (local)
//		String jsonld = "jsonld" + File.separator + "led.jsonld";
//		// led (URL)
//		URL jsonld = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/led.jsonld");
		// door
		 URL jsonld = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/door.jsonld");
		
		ClientFactory cf = new ClientFactory();
		@SuppressWarnings("unused")
		Client client = cf.getClient(jsonld);
		
	}
}
