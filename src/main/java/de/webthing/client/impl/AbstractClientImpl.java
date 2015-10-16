package de.webthing.client.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;

import de.webthing.client.Client;
import de.webthing.desc.DescriptionParser;
import de.webthing.desc.pojo.ActionDescription;
import de.webthing.desc.pojo.EventDescription;
import de.webthing.desc.pojo.InteractionDescription;
import de.webthing.desc.pojo.Metadata;
import de.webthing.desc.pojo.PropertyDescription;
import de.webthing.desc.pojo.Protocol;
import de.webthing.desc.pojo.ThingDescription;

public abstract class AbstractClientImpl implements Client{
	
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

		System.out.println("# Interactions");
		List<InteractionDescription> interactions = td.getInteractions();
		for(InteractionDescription id : interactions) {
			String iname = id.getName();
			System.out.println(iname);
			if(id instanceof ActionDescription) {
				ActionDescription ad = (ActionDescription) id;
				System.out.println("\tinput:  " + ad.getInputType());
				System.out.println("\toutput: " + ad.getOutputType());
				actions.add(ad);
			} else if(id instanceof PropertyDescription) {
				PropertyDescription pd = (PropertyDescription) id;
				System.out.println("\toutput: " + pd.getOutputType());
				System.out.println("\twritable: " + pd.isWritable());
				properties.add(pd);
			} else if(id instanceof EventDescription) {
				EventDescription ed = (EventDescription) id;
				System.out.println("\toutput: " + ed.getOutputType());
				events.add(ed);
			} else {
				System.out.println("Unexpted interaction type: " + id);
			}
		}
		
		Metadata metadata = td.getMetadata();
		System.out.println("# Metadata " + metadata.getName());
		System.out.println("# Encodings");
		List<String> encs = metadata.getEncodings();
		for(String enc : encs) {
			System.out.println(enc);
			encodings.add(enc);
		}
		System.out.println("# Encodings");
		Map<String,Protocol> prots = metadata.getProtocols();
		for(String ps : prots.keySet()) {
			System.out.println(ps);
			Protocol p = prots.get(ps);
			protocols.add(p);
			System.out.println("\t" + p.getUri());
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
