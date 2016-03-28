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

package de.thingweb.desc.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johannes on 02.09.2015.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ThingDescription {
    
	@JsonIgnore
    private Metadata metadata;
	
    @JsonProperty("@type")
    @JsonInclude(Include.NON_NULL)
    protected String thingType;

    //No longer a Json property
    @JsonIgnore
    private List<InteractionDescription> interactions;
    
    @JsonIgnore
    private List<Pair<String,String>> additionalContexts;
    
    @JsonProperty
    @JsonInclude(Include.NON_NULL)
    private List<PropertyDescription> properties;
    
    @JsonProperty
    @JsonInclude(Include.NON_NULL)
    private List<ActionDescription> actions;
    
    @JsonProperty
    @JsonInclude(Include.NON_NULL)
    private List<EventDescription> events;
    
    @JsonProperty
    private String name;
    
    @JsonProperty
    @JsonInclude(Include.NON_NULL)
    private Object security;

    @JsonProperty
    private List<String> uris;

    @JsonProperty
    private List<String> encodings;
    

    
    @JsonIgnore
    public static final String WOT_TD_CONTEXT = "http://w3c.github.io/wot/w3c-wot-td-context.jsonld";
    
    public ThingDescription(Metadata metadata, List<InteractionDescription> interactions, String type){
    	this(metadata, interactions);
    	this.thingType = type;
    }
    
    public ThingDescription(Metadata metadata, List<InteractionDescription> interactions) {
      this.metadata = metadata;
      this.name = metadata.getName();
      this.security = metadata.getSecurityDescription();
      this.uris = metadata.getProtocols();
      this.encodings = metadata.getEncodings();
      
      this.interactions = interactions;  
      
      for(InteractionDescription id : interactions){
    	  if(id instanceof PropertyDescription){
    		  if(properties == null)
    			  properties = new ArrayList<>();
    		  properties.add((PropertyDescription)id);
    	  }
    	  else if(id instanceof ActionDescription){
    		  if(actions == null)
    			  actions = new ArrayList<>();
    		  actions.add((ActionDescription)id);
    	  }
    	  else if(id instanceof EventDescription){
    		  if(events == null)
    			  events = new ArrayList<>();
    		  events.add((EventDescription)id);
    	  }
      }
    }
    
    @JsonCreator
    public ThingDescription(@JsonProperty("name") String name, @JsonProperty("@type") String type, @JsonProperty("uris") List<String> protocols, @JsonProperty("encodings") List<String> encodings, @JsonProperty("security") Object security, @JsonProperty("properties") List<PropertyDescription> properties, @JsonProperty("actions") List<ActionDescription> actions, @JsonProperty("events") List<EventDescription> events){
        this.name = name;
        this.encodings = encodings;
        this.uris = protocols;
        this.security = security;
        this.properties = properties;
        this.actions = actions;
        this.events = events;
        this.thingType = type;
    }
    
    public Metadata getMetadata() {
      return metadata;
    }
    
    public List<InteractionDescription> getInteractions() {
      return interactions;
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
    
    public List<Pair<String,String>> getAdditionalContexts(){
    	return additionalContexts;
    }
    
    public void setAdditionalContexts(List<Pair<String,String>> contexts){
    	additionalContexts = contexts;
    }
}
