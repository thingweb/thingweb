package de.webthing.desc.pojo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Metadata {
    
    @JsonProperty
    private String name;
    
    @JsonProperty
    private Map<String, Protocol> protocols;
    
    @JsonProperty
    private List<String> encodings;
    
    @JsonCreator
    public Metadata(@JsonProperty("name") String name, @JsonProperty("protocols") Map<String, Protocol> protocols, @JsonProperty("encodings") List<String> encodings) {
	this.name = name;
	this.encodings = encodings;
	this.protocols = protocols;
    }
    
    public String getName() {
	return name;
    }
    
    public Map<String, Protocol> getProtocols() {
	return protocols;
    }
    
    public List<String> getEncodings() {
	return encodings;
    }
    
}