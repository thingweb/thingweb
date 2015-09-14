package de.webthing.desc.pojo;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class Protocol {
    
    @JsonProperty
    public String uri;
    
    @JsonProperty
    public Integer priority;
    
    @JsonCreator
    public Protocol(@JsonProperty("uri") String uri, @JsonProperty("priority") Integer priority) {
	this.uri = uri;
	this.priority = priority;
    }
    
}