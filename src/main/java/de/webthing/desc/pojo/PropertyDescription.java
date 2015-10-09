package de.webthing.desc.pojo;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("Property")
public class PropertyDescription extends InteractionDescription {

    @JsonProperty
    private boolean writable;
    
    @JsonProperty("outputData")
    private String outputType;
    
    @JsonCreator
    public PropertyDescription(@JsonProperty("name") String name, @JsonProperty("writable") Boolean writable, @JsonProperty("outputData") String outputType) {
	this.name = name;
	this.writable = writable;
	this.outputType = outputType;
    }
    
    public String getOutputType() {
	return outputType;
    }
    
    public boolean isWritable() {
	return writable;
    }

}