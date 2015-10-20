package de.webthing.desc.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Johannes on 02.09.2015.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ThingDescription {
    
    @JsonProperty
    private Metadata metadata;

    @JsonProperty
    private List<InteractionDescription> interactions;
    
    @JsonCreator
    public ThingDescription(@JsonProperty("metadata") Metadata metadata, @JsonProperty("interactions") List<InteractionDescription> interactions) {
	this.metadata = metadata;
	this.interactions = interactions;
    }
    
    public Metadata getMetadata() {
	return metadata;
    }
    
    public List<InteractionDescription> getInteractions() {
	return interactions;
    }
    
}
