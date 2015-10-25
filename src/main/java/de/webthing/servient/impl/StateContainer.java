/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Siemens AG and the thingweb community
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package de.webthing.servient.impl;

import de.webthing.servient.ThingServer;
import de.webthing.thing.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


/**
 * The StateContainer saves all state belonging to a {@link ThingServer}.
 */
public class StateContainer {

	protected static final Logger log = LoggerFactory.getLogger(StateContainer.class);

	public StateContainer(Thing thingModel) {
		for (Property property : thingModel.getProperties()) {
			m_values.put(property, new Content("".getBytes(), MediaType.TEXT_PLAIN));
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
//	private final Map<Action, List<Callable<Object>>> m_callbacks = new HashMap<>();
	private final Map<Action, Function<?, ?>> m_handlers = new HashMap<>();
}
