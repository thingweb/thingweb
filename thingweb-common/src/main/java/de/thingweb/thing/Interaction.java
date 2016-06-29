package de.thingweb.thing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

public class Interaction extends Observable {

	private Metadata m_metadata = new Metadata();
	private Object m_tag;
	
	
	public Metadata getMetadata(){
		return m_metadata;
	}


	public Object getTag() {
		return m_tag;
	}


	public void setTag(Object m_tag) {
		this.m_tag = m_tag;
	}
	
	

}
