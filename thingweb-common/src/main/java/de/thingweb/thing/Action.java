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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.thingweb.thing.Property.Builder;

public class Action extends Interaction {

    private final Map<String, String> params;
    private final String name;
    private final String inputType;
    private final String outputType;
    private final List<String> hrefs;

    @Deprecated
    protected Action(String name, Map<String, String> params) {
        this(name, params.get("parm"), "", new ArrayList<String>());
    }
    
    protected Action(String name, String inputType, String outputType, List<String> hrefs) {
        this.params = new HashMap<>();
        this.name = name;
        this.inputType = inputType;
        this.outputType = outputType;
        this.hrefs = hrefs;
    }

    /**
     * creates a Builder for an Action
     * @param name the Name of the Action
     * @return a {@link de.thingweb.thing.Action.Builder} for the action
     * */
    public static Action.Builder getBuilder(String name) {
        return new Action.Builder(name);
    }

    public String getInputType() {
        return inputType;
    }

    public String getOutputType() {
        return outputType;
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

    public static class Builder {
        private final Map<String,String> params = new HashMap<>();

        private final String name;
        private String inputType = "";
        private String outputType = "";
        private List<String> hrefs = new ArrayList<String>();

        private Builder(String name) {
            this.name = name;
        }

        public Builder setInputType(String inputType) {
            this.inputType = (inputType == null) ? "" : inputType;
            return this;
        }

        public Builder setOutputType(String outputType) {
            this.outputType = (outputType == null) ? "" : outputType;
            return this;
        }
        
        public Builder setHrefs(List<String> hrefs) {
          this.hrefs = hrefs;
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
                    return new Action(name,inputType,outputType, hrefs);
               else
                       return new Action(name,params);
        }
    }
}
