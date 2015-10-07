package de.webthing.servient.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import de.webthing.servient.ThingServer;
import de.webthing.thing.Action;
import de.webthing.thing.Property;
import de.webthing.thing.Thing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The StateContainer saves all state belonging to a {@link ThingServer}.
 */
public class StateContainer {

	protected static final Logger log = LoggerFactory.getLogger(StateContainer.class);

	public StateContainer(Thing thingModel) {
		for (Property property : thingModel.getProperties()) {
			m_values.put(property, "");
		}

		for (Action action : thingModel.getActions()) {
			m_handlers.put(action, Void -> {log.info("unhandled action " + action.getName() + " called"); return Void;});
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

	public void addHandler(Action action, Function<?, ?> handler) {
		if (null == action) {
			throw new IllegalArgumentException("action must not be null");
		}
		if (null == handler) {
			throw new IllegalArgumentException("handler must not be null");
		}
		if (!m_handlers.containsKey(action)) {
			throw new IllegalArgumentException("Unknown action: " + action);
		}

		Function<?, ?> oldhandler = m_handlers.get(action);
		if(oldhandler != null) {
			log.info("replacing existing handler.");
		}
		m_handlers.put(action, handler);
	}

	public Function<?, ?> getHandler(Action action) {
		if (null == action) {
			throw new IllegalArgumentException("action must not be null");
		}
		if (!m_handlers.containsKey(action)) {
			throw new IllegalArgumentException("Unknown action: " + action);
		}

		return m_handlers.get(action);
	}
	
	
	private final Map<Property,Object> m_values = new HashMap<>();
	private final Map<Action, Function<?, ?>> m_handlers = new HashMap<>();
}
