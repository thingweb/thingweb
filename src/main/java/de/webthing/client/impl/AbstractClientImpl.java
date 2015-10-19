package de.webthing.client.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.webthing.client.Client;
import de.webthing.desc.DescriptionParser;
import de.webthing.desc.pojo.ActionDescription;
import de.webthing.desc.pojo.EventDescription;
import de.webthing.desc.pojo.InteractionDescription;
import de.webthing.desc.pojo.Metadata;
import de.webthing.desc.pojo.PropertyDescription;
import de.webthing.desc.pojo.Protocol;
import de.webthing.desc.pojo.ThingDescription;

public abstract class AbstractClientImpl implements Client {
	
	private static final Logger log = LoggerFactory.getLogger(AbstractClientImpl.class);
	
	ThingDescription td;
	List<ActionDescription> actions;
	List<PropertyDescription> properties;
	List<EventDescription> events;

	List<String> encodings;
	List<Protocol> protocols;

	
	public AbstractClientImpl() {
	}
	
	public void parse(URL jsonld) throws JsonParseException, IOException {
		td = DescriptionParser.fromURL(jsonld);
		processThingDescription();
	}
	
	public void parse(String jsonld) throws FileNotFoundException, IOException {
		td = DescriptionParser.fromFile(jsonld);
		processThingDescription();
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
	
	public List<PropertyDescription> getProperties() {
		return properties;
	}
	
	public List<ActionDescription> getActions() {
		return actions;
	}
	
	public List<EventDescription> getEvents() {
		return events;
	}
	
	public List<Protocol> getProtocols() {
		return protocols;
	}
	

}
