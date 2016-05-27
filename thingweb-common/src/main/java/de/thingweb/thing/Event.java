package de.thingweb.thing;

import java.util.ArrayList;
import java.util.List;

public class Event extends Interaction {
	
	private final String name;
	private final String valueType;
	private final List<String> hrefs;
	 
    protected Event(String name, String valueType, List<String> hrefs) {
        this.name = name;
        this.valueType = valueType;
        this.hrefs = hrefs;
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
    
	public String getValueType() {
		return valueType;
	}

	public List<String> getHrefs(){
		return hrefs;
	}
	
    public static class Builder {

        private final String name;
        private String valueType = "";
        private List<String> hrefs = new ArrayList<String>();

        private Builder(String name) {
            this.name = name;
        }

        public Builder setValueType(String valueType) {
            this.valueType = (valueType == null) ? "" : valueType;
            return this;
        }
        
        public Builder setHrefs(List<String> hrefs) {
          this.hrefs = hrefs;
          return this;
        }

        /**
         * generate the event, finalize the Builder
         * @return the constructed Event
         */
        public Event build() {
           return new Event(name, valueType, hrefs);
        }
    }
}
