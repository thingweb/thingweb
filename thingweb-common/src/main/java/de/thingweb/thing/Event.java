package de.thingweb.thing;

import java.util.ArrayList;
import java.util.List;

public class Event extends Action {
	 
    protected Event(String name, String inputType, String outputType, List<String> hrefs) {
    	super(name,inputType,outputType,hrefs);
    }


}
