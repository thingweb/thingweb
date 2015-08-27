package de.webthing.servient.impl;

import java.util.HashMap;
import java.util.Map;

import de.webthing.servient.ThingServer;
import de.webthing.thing.Property;
import de.webthing.thing.Thing;


/**
 * The StateContainer saves all state belonging to a {@link ThingServer}.
 */
public class StateContainer {
	
	public StateContainer(Thing thingModel) {
		for (Property property : thingModel.getProperties()) {
			m_values.put(property, "");
		}
	}
	

	public void setProperty(Property property, Object value) {
		if (null == property) {
			throw new IllegalArgumentException("property must not be null");
		}
		if (null == value) {
			throw new IllegalArgumentException("value must not be null");
		}
		if (!m_values.containsKey(property)) {
			throw new IllegalArgumentException("Unknown property: " + property);
		}
		
		// FIXME: add type / compatibility check between value and type info
		// from property
		
		m_values.put(property, value);
	}
	
	
	public Object getProperty(Property property) {
		if (null == property) {
			throw new IllegalArgumentException("property must not be null");
		}
		if (!m_values.containsKey(property)) {
			throw new IllegalArgumentException("Unknown property: " + property);
		}
		
		return m_values.get(property);
	}
	
	
	private final Map<Property,Object> m_values = new HashMap<>();
}
