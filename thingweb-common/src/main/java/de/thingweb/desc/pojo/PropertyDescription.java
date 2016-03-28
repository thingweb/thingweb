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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonTypeName("Property")
@JsonIgnoreProperties(ignoreUnknown=true)
public class PropertyDescription extends InteractionDescription {

    @JsonProperty
    private Boolean writable;
    
    @JsonProperty("valueType")
    private String valueType;
    
    @JsonProperty
    @JsonInclude(Include.NON_NULL)
    private Integer stability;
    
    //@JsonCreator
    //public PropertyDescription(@JsonProperty("name") String name, @JsonProperty("writable") Boolean writable, @JsonProperty("valueType") String outputType) {
    //  this(name,writable,outputType, null);      
    //}
    
    @JsonCreator
    public PropertyDescription(@JsonProperty("name") String name, @JsonProperty("@id") String id, @JsonProperty("writable") Boolean writable, @JsonProperty("valueType") String outputType, @JsonProperty("hrefs")List<String> hrefs, @JsonProperty("@type") String propertyType, @JsonProperty("@stability") Integer stability) {
      this.name = name;
      this.writable = writable;
      this.valueType = outputType;
      this.interactionType = propertyType;
      this.hrefs = hrefs;
      this.id = id;
      this.stability = stability;
    }
    
    public PropertyDescription(String name, Boolean writeable, String valueType){
    	this(name, null, writeable, valueType, null, null, null);
    }
    
    public PropertyDescription(String name){
    	this(name, null, false, "xsd:string", null, null, null);
    }

    
    public String getOutputType() {
      return valueType;
    }
    
    public boolean isWritable() {
      return writable;
    }

    public boolean isReadable() {
        return true; //TODO: is this a part of TD?
      }
    
    public Integer getStability(){
    	return stability;
    }
    
    public PropertyDescription clone(){
    	return new PropertyDescription(name, id, writable, valueType, hrefs, interactionType, stability);
    }
    
    public static class Builder {
		private String name;
		private String id;
		private boolean isReadable = true;
		private boolean isWriteable = false;
		private String valueType = "xsd:string";
		private String propertyType = null;
		private List<String> hrefs = new ArrayList<>();
		private Integer stability = null;

		public Builder(String name) {
			this.name = name;
		}

		public Builder setValueType(String valueType) {
			this.valueType = valueType;
			return this;
		}
		
		public Builder setPropertyType(String propertyType) {
			this.propertyType = propertyType;
			return this;
		}
		public Builder setHrefs(List<String> hrefs) {
			this.hrefs = hrefs;
			return this;
		}

		public PropertyDescription.Builder setName(String name) {
			this.name = name;
			return this;
		}

		public PropertyDescription.Builder setReadable(boolean isReadable) {
			this.isReadable = isReadable;
			return this;
		}

		public PropertyDescription.Builder setWriteable(boolean isWriteable) {
			this.isWriteable = isWriteable;
			return this;
		}
		
		public PropertyDescription.Builder setId(String id) {
			this.id = id;
			return this;
		}
		
		public PropertyDescription.Builder setStability(Integer stability) {
			this.stability = stability;
			return this;
		}
		 
		public PropertyDescription build() {
			return new PropertyDescription(name, id, isWriteable, valueType, hrefs, propertyType, stability);
		}
	}    

}