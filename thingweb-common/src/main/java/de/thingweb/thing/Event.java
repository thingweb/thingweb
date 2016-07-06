package de.thingweb.thing;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class Event {
	
	private final String name;
	private final JsonNode valueType;
	private final String eventType;
	private final List<String> hrefs;
	private final JsonNode security;
	 
    protected Event(String name, JsonNode valueType, String eventType, List<String> hrefs, JsonNode security) {
        this.name = name;
        this.eventType = eventType;
        this.valueType = valueType;
        this.hrefs = hrefs;
        this.security = security;
    }
    
    /**
     * creates a Builder for an Event
     * @param name the Name of the Event
     * @return a {@link de.thingweb.thing.Event.Builder} for the action
     * */
    public static Event.Builder getBuilder(String name) {
        return new Event.Builder(name);
    }
    
	
    public String getName() {
        return name;
    }
    
	public JsonNode getValueType() {
		return valueType;
	}
	
	public String getEventType(){
		return eventType;
	}

	public List<String> getHrefs(){
		return hrefs;
	}
	
	public JsonNode getSecurity(){
		return security;
	}
	
	
    public static class Builder {

        private final String name;
        private JsonNode valueType;
        private String eventType = null;
        private List<String> hrefs = new ArrayList<String>();
        private JsonNode security = null;

        private Builder(String name) {
            this.name = name;
        }

        @Deprecated
        public Builder setValueType(String valueType) {
        	return setValueType(JsonNodeFactory.instance.textNode(valueType));
        }
        
        public Builder setValueType(JsonNode valueType) {
            this.valueType = valueType;
            return this;
        }
        
        public Builder setEventType(String eventType) {
            this.eventType = eventType;
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
         * generate the event, finalize the Builder
         * @return the constructed Event
         */
        public Event build() {
           return new Event(name, valueType, eventType, hrefs, security);
        }
    }
}
