package de.thingweb.thing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

public class Interaction extends Observable {

	private Metadata m_metadata = new Metadata();
	
	public Metadata getMetadata(){
		return m_metadata;
	}

}
