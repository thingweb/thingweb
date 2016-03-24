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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("Property")
@JsonIgnoreProperties(ignoreUnknown=true)
public class PropertyDescription extends InteractionDescription {

    @JsonProperty
    private Boolean writable;
    

    
    @JsonProperty("valueType")
    private String valueType;
    

    
    //@JsonCreator
    //public PropertyDescription(@JsonProperty("name") String name, @JsonProperty("writable") Boolean writable, @JsonProperty("valueType") String outputType) {
    //  this(name,writable,outputType, null);      
    //}
    
    @JsonCreator
    public PropertyDescription(@JsonProperty("name") String name, @JsonProperty("writable") Boolean writable, @JsonProperty("valueType") String outputType, @JsonProperty("hrefs")List<String> hrefs, @JsonProperty("@type") String propertyType) {
      this.name = name;
      this.writable = writable;
      this.valueType = outputType;
      this.propertyType = propertyType;
      this.hrefs = hrefs;
    }
    

    
    public String getOutputType() {
      return valueType;
    }
    
    public boolean isWritable() {
      return writable;
    }
    

}