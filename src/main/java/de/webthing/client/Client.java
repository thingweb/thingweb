package de.webthing.client;

import com.fasterxml.jackson.core.JsonParseException;
import de.webthing.desc.pojo.ActionDescription;
import de.webthing.desc.pojo.EventDescription;
import de.webthing.desc.pojo.PropertyDescription;
import de.webthing.desc.pojo.Protocol;
import de.webthing.thing.Content;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public interface Client {
	
	public void parse(URL jsonld) throws JsonParseException, IOException;
	
	public void parse(String jsonld) throws FileNotFoundException, IOException;
	
	public List<PropertyDescription> getProperties();
	
	public List<ActionDescription> getActions();
	
	public List<EventDescription> getEvents();
	
	public List<Protocol> getProtocols();
	
	public String getUsedProtocolURI();
	public String getUsedEncoding();
	
	
	public void put(String propertyName, Content propertyValue, Callback callback);
	
	public void get(String propertyName, Callback callback);
	
	public void observe(String propertyName, Callback callback);
	
	public void observeRelease(String propertyName);
	
	public void action(String actionName, Content actionValue, Callback callback);

}
