package de.webthing.desc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="@type")
@JsonSubTypes({@JsonSubTypes.Type(PropertyDescription.class), @JsonSubTypes.Type(ActionDescription.class)})
public abstract class InteractionDescription {
    
    @JsonProperty
    protected String name;
    
    public String getName() {
	return name;
    }

}