package de.webthing.desc.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("Event")
public class EventDescription extends InteractionDescription {
    
    @JsonProperty("outputData")
    private String outputType;
    
    @JsonCreator
    public EventDescription(@JsonProperty("name") String name, @JsonProperty("outputData") String outputType) {
	this.name = name;
	this.outputType = outputType;
    }
    
    public String getOutputType() {
	return outputType;
    }
    
}