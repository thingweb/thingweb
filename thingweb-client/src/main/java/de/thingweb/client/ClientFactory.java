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
import de.thingweb.desc.DescriptionParser;
import de.thingweb.desc.pojo.*;
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
	
	ThingDescription td;
	Metadata metadata;
	List<PropertyDescription> properties;
	List<ActionDescription> actions;
	List<EventDescription> events;

	List<String> encodings;
	List<Protocol> protocols;
	
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
			td = DescriptionParser.fromBytes(content);
			
			return getClient();
		} else {
			return getClientUrl(jsonld.toURL());
		}	
	}
	
	protected Client getClient() throws UnsupportedException, URISyntaxException {
		assert(td != null);
		
		processThingDescription();
		
		// pick the right client
		return pickClient();
	}
	
	public Client getClientUrl(URL jsonld) throws JsonParseException, IOException, UnsupportedException, URISyntaxException {
		// Note: URL can't handle coap --> needs to be done before
		td = DescriptionParser.fromURL(jsonld);
		return getClient();
	}
	
	public Client getClientFile(String jsonld) throws FileNotFoundException, IOException, UnsupportedException, URISyntaxException {
		td = DescriptionParser.fromFile(jsonld);
		return getClient();
	}
	
	public Client getClientFromTD(ThingDescription thingDescription) throws UnsupportedException, URISyntaxException {
		td = thingDescription;
		return getClient();
	}

	protected void processThingDescription() {
		assert(td != null);
		
		actions = new ArrayList<>();
		properties = new ArrayList<>();
		events = new ArrayList<>();
		encodings = new ArrayList<>();
		protocols = new ArrayList<>();

		log.debug("Process thing desription");
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
				boolean isWritable = true;
				try {
					// @Workaround repository
					isWritable = pd.isWritable();
				} catch (Exception e) {
					// PropertyDescription coming from discovery seems to be different and causes issues
					log.warn("Workaround for isWritable issue kicked in. Writable set by default to " + isWritable);
					pd = new PropertyDescription(pd.getName(), null, isWritable, pd.getOutputType(), pd.getHrefs(), pd.getInteractionType(), pd.getStability());
				}
				log.debug("\twritable: " + isWritable);
				properties.add(pd);
			} else if(id instanceof EventDescription) {
				EventDescription ed = (EventDescription) id;
				log.debug("\toutput: " + ed.getOutputType());
				events.add(ed);
			} else {
				log.warn("Unexpected interaction type: " + id);
			}
		}
		
		metadata = td.getMetadata();
		log.debug("# Metadata " + metadata.getName());
		log.debug("# Encodings");
		List<String> encs = metadata.getEncodings();
		// @Workaround repository
		if(encs == null) {
			// Information coming from discovery seems to be different and causes issues
			log.warn("Workaround for TD encodings issue kicked in. Encoding set to " + "JSON");
			encs = new ArrayList<>();
			encs.add("JSON");
		}
		for(String enc : encs) {
			log.debug(enc);
			encodings.add(enc);
		}
		log.debug("# Encodings");
		List<String> uris = metadata.getProtocols();
		int i=1;
		for(String uri : uris) {
			log.debug(uri);
			Protocol p = new Protocol(uri,i++);
			// @Workaround repository
			if(p.getPriority() == null) {
				// Information coming from discovery seems to be different and causes issues
				log.warn("Workaround for TD property issue kicked in. Priority set to " + 1);
				p.priority = 1;
			}
			
			protocols.add(p);
			log.debug("\t" + p.getUri());
			// clean-up URI (remove appended URI slash if any)
			if(uri.endsWith("/")) {
				uri = uri.substring(0, uri.length()-1);
				p.uri = uri;
				log.debug("\t\t" + "clean-up URI by removing trailing '/'");
			}
		}
	}

	protected Client pickClient() throws UnsupportedException, URISyntaxException {
		// check for right protocol&encoding
		TreeMap<Integer, Client> tm = new TreeMap<>(); // sorted according priority
		
		
		for(Protocol p : protocols) {
			String suri = p.getUri();
			if(suri != null && suri.length()>0){
				URI uri = new URI(suri);
				if(isCoapScheme(uri.getScheme())) {
					Client c = new CoapClientImpl(p, metadata, properties, actions, events);
					tm.put(p.priority, c);
					log.info("Found matching client '" + CoapClientImpl.class.getName() + "' with priority " + p.priority);
				} else if(isHttpScheme(uri.getScheme())) {
					Client c = new HttpClientImpl(p, metadata, properties, actions, events);
					tm.put(p.priority, c);
					log.info("Found matching client '" + HttpClientImpl.class.getName() + "' with priority " + p.priority);
				}				
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
