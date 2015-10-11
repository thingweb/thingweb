package de.webthing.desc.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("Action")
public class ActionDescription extends InteractionDescription {
    
    @JsonProperty("outputData")
    private String outputType;
    
    @JsonProperty("inputData")
    private String inputType;
    
    @JsonCreator
    public ActionDescription(@JsonProperty("name") String name, @JsonProperty("inputData") String outputType, @JsonProperty("outputData") String inputType) {
	this.name = name;
	this.outputType = outputType;
	this.inputType = inputType;
    }

    public String getInputType() {
	return inputType;
    }
    
    public String getOutputType() {
	return outputType;
    }
    
}