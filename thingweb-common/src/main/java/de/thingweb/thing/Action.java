/*
 *
 *  * The MIT License (MIT)
 *  *
 *  * Copyright (c) 2016 Siemens AG and the thingweb community
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in
 *  * all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  * THE SOFTWARE.
 *
 */

package de.thingweb.thing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Action {

    private final Map<String, String> params;
    private final String name;
    private final JsonNode inputType;
    private final JsonNode outputType;
    private final String actionType;
    private final List<String> hrefs;
    private final JsonNode security;

    @Deprecated
    protected Action(String name, Map<String, String> params) {
        this(name, null,null, null, new ArrayList<String>(), null);
    }
    
    protected Action(String name, JsonNode inputType, JsonNode outputType, String actionType, List<String> hrefs, JsonNode security) {
        this.params = new HashMap<>();
        this.name = name;
        this.inputType = inputType;
        this.outputType = outputType;
        this.actionType = actionType;
        this.hrefs = hrefs;
        this.security = security;
    }

    /**
     * creates a Builder for an Action
     * @param name the Name of the Action
     * @return a {@link de.thingweb.thing.Action.Builder} for the action
     * */
    public static Action.Builder getBuilder(String name) {
        return new Action.Builder(name);
    }

    public JsonNode getInputType() {
        return inputType;
    }

    public JsonNode getOutputType() {
        return outputType;
    }
    
    public String getActionType() {
        return actionType;
    }

    @Deprecated
    public Map<String, String> getParams() {
        return params;
    }

    public String getName() {
        return name;
    }
    
    public List<String> getHrefs(){
      return hrefs;
    }
    
	public JsonNode getSecurity(){
		return security;
	}

    public static class Builder {
        private final Map<String,String> params = new HashMap<>();

        private final String name;
        private JsonNode inputType = null;
        private JsonNode outputType = null;
        private String actionType = null;
        private List<String> hrefs = new ArrayList<String>();
		/**
		 * [optional] Access metadata (self-contained) for protecting this Property and securely transmitting information. Compared to the security field that can be found in the Thing metadata, this field here can be used to apply specific security requirements that is only valid for this resource.
		 */
		private JsonNode security = null;

        private Builder(String name) {
            this.name = name;
        }

        @Deprecated
        public Builder setInputType(String inputType) {
            this.inputType = JsonNodeFactory.instance.textNode(inputType);
            return this;
        }

        @Deprecated
        public Builder setOutputType(String outputType) {
            this.outputType = JsonNodeFactory.instance.textNode(outputType);
            return this;
        }
        
		public Builder setActionType(String actionType) {
			this.actionType = actionType;
			return this;
		}
        
        public Builder setHrefs(List<String> hrefs) {
          this.hrefs = hrefs;
          return this;
        }
        
		public Builder setSecurity(JsonNode security) {
			this.security = security;
			return this;
		}

        /**
         * add a parameter / input value to the action
         * @param name name of the parameter
         * @param type Type of the parameter
         * @return the Builder (fluent Method call)
         * @deprecated spec addresses single input and output type definitions, use structured types
         * */
        @Deprecated
        public Action.Builder addParam(String name, String type) {
            this.params.put(name,type);
            return this;
        }

        /**
         * generate the action, finalize the Builder
         * @return the constructed Action
         */
        public Action build() {
            if(params.size() == 0)
                    return new Action(name,inputType,outputType, actionType, hrefs, security);
               else
                       return new Action(name,params);
        }

        public Builder setInputType(JsonNode jnI) {
            this.inputType = jnI;
            return this;
        }

        public Builder setOutputType(JsonNode jnO) {
            this.outputType = jnO;
            return this;
        }
    }
}
