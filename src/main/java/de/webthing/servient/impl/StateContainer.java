package de.webthing.servient.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import de.webthing.servient.ThingServer;
import de.webthing.thing.Action;
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

		for (Action action : thingModel.getActions()) {
			m_callbacks.put(action,new LinkedList<Callable<Object>>());
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

	public void addCallback(Action action, Callable<Object> callback) {
		if (null == action) {
			throw new IllegalArgumentException("action must not be null");
		}
		if (null == callback) {
			throw new IllegalArgumentException("callback must not be null");
		}
		if (!m_callbacks.containsKey(action)) {
			throw new IllegalArgumentException("Unknown action: " + action);
		}

		List<Callable<Object>> cblist = m_callbacks.get(action);
		if(cblist == null) {
			//paranoia mode: this should never happen
			cblist = new LinkedList<>();
			m_callbacks.put(action,cblist);
		}
		cblist.add(callback);
	}

	public List<Callable<Object>> getCallbacks(Action action) {
		if (null == action) {
			throw new IllegalArgumentException("action must not be null");
		}
		if (!m_callbacks.containsKey(action)) {
			throw new IllegalArgumentException("Unknown action: " + action);
		}

		return m_callbacks.get(action);
	}
	
	
	private final Map<Property,Object> m_values = new HashMap<>();
	private final Map<Action, List<Callable<Object>>> m_callbacks = new HashMap<>();
}
