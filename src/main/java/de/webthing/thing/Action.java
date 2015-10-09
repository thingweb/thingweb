package de.webthing.thing;

import java.util.HashMap;
import java.util.Map;

public class Action {

    private final Map<String, String> params;
    private final String name;

    protected Action(String name,Map<String, String> params) {
        this.params = params;
        this.name = name;
    }

    /**
     * creates a Builder for an Action
     * @param name the Name of the Action
     * @return a {@link de.webthing.thing.Action.Builder} for the action
     * */
    public static Action.Builder getBuilder(String name) {
        return new Action.Builder(name);
    }

    public Map<String, String> getParams() {
        return params;
    }

    public String getName() {
        return name;
    }


    public static class Builder {
        private final Map<String,String> params = new HashMap<>();

        private final String name;

        private Builder(String name) {
            this.name = name;
        }

        /**
         * add a parameter / input value to the action
         * @param name name of the parameter
         * @param type Type of the parameter
         * @return the Builder (fluent Method call)
         * */
        public Action.Builder addParam(String name, String type) {
            this.params.put(name,type);
            return this;
        }

        /**
         * generate the action, finalize the Builder
         * @return the constructed Action
         */
        public Action build() {
            return new Action(name,params);
        }
    }
}
