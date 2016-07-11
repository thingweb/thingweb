/*
 *
 *  * The MIT License (MIT)
 *  *
 *  * Copyright (c) 2016 Siemens AG and the thingweb community
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in
 *  * all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  * THE SOFTWARE.
 *
 */

package de.thingweb.thing;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;


/**
 * The Thing class provides the "model" of a thing, the components and
 * capabilities of a thing.
 */
public final class Thing {
    /*
	 * Implementation Note:
	 * This implementation is based on the assumption that change is rare, i.e.,
	 * interactions with the model heavily outweigh modifications of the model.
	 * If this is no longer the case, the type of concurrent collection used
	 * should be changed or other means of synchronization should be used.
	 * 
	 * This class is final to ensure the synchronization guarantees are not
	 * broken by subclasses. 
	 */

    private final String m_name;
    private final Metadata m_metadata;

    /**
     * Creates a new thing model.
     * <p>
     * The thing's name is used to create unique URLs for different things,
     * i.e., URLs formed like <code>/things/&lt;name&gt;/...</code>. Names must
     * therefore be unique per ThingServer.
     *
     * @param name the name, must not be null
     */
    public Thing(String name) {
        if (null == name) {
            throw new IllegalArgumentException("name must not be null");
        }

        m_name = name;
        m_metadata = new Metadata();
    }

    public String getName() {
        return m_name;
    }
    
	public URI getUri(int index) {
		JsonNode uris = getMetadata().get("uris");
		if (uris != null) {
			try {
				if (uris.getNodeType() == JsonNodeType.STRING) {
					return new URI(uris.asText());
				} else if (uris.getNodeType() == JsonNodeType.ARRAY) {
					ArrayNode an = (ArrayNode)uris;
					return new URI(an.get(index).asText());
				}
			} catch (URISyntaxException e) {
				throw new RuntimeException("TD with malformed base uris");
			}
		} else {
			throw new RuntimeException("TD without base uris field");
		}
		// should never be reached
		throw new RuntimeException("Unexpected error while retrieving uri at index " + index);
	}
    
    public String resolvePropertyUri(String name, int index) {
    	
    	URI uri = getUri(index);
    	
    	Property p = getProperty(name);
    	
    	if (p!=null) {
    		try {
    			// String scheme, String userInfo, String host, int port, String path, String query, String fragment
    			String path = uri.getPath();
    			if(path.endsWith("/")) {
    				path = path + p.getHrefs().get(index);
    			} else {
    				path = path + "/" + p.getHrefs().get(index);
    			}
    			uri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), path, uri.getQuery(), uri.getFragment());
			} catch (URISyntaxException e) {
				throw new RuntimeException("TD with malformed hrefs");
			}
    	} else {
    		throw new RuntimeException("No such Property");
    	}
    	
    	return uri.toString();
    }
    
    public String resolveActionUri(String name, int index) {
    	
    	URI uri = getUri(index);
    	
    	Action a = getAction(name);
    	
    	if (a!=null) {
    		try {
    			String path = uri.getPath();
    			if(path.endsWith("/")) {
    				path = path + a.getHrefs().get(index);
    			} else {
    				path = path + "/" + a.getHrefs().get(index);
    			}
    			uri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), path, uri.getQuery(), uri.getFragment());
			} catch (URISyntaxException e) {
				throw new RuntimeException("TD with malformed hrefs");
			}
    	} else {
    		throw new RuntimeException("No such Property");
    	}
    	
    	return uri.toString();
    }

    public Metadata getMetadata() {
        return m_metadata;
    }


    public List<Property> getProperties() {
        return m_propertiesView;
    }

    
    public List<Event> getEvents() {
        return m_eventsView;
    }

    public List<Action> getActions() {
        return m_actionsView;
    }


    /**
     * Returns a property by name.
     *
     * @param propertyName the name of the property, must not be null
     * @return the property, or null if no property with the given name exists
     */
    public Property getProperty(String propertyName) {
        if (null == propertyName) {
            throw new IllegalArgumentException("propertyName must not be null");
        }

        for (Property property : m_properties) {
            if (property.getName().equals(propertyName)) {
                return property;
            }
        }

        return null;
    }


    /**
     * Returns a property by name and raises an exception if the property does
     * not exist.
     * <p>
     * Behaves similarly to {@link #getProperty(String)} but throws an
     * exception instead of returning null if the property does not exist
     *
     * @param propertyName the name of the property, must not be null
     * @return the property
     */
    public Property getRequiredProperty(String propertyName) {
        Property p = getProperty(propertyName);

        if (null == p) {
            throw new IllegalArgumentException("no such property " +
                    propertyName);
        }

        return p;
    }


    public boolean isOwnerOf(Property property) {
        if (null == property) {
            throw new IllegalArgumentException("property must not be null");
        }

        for (Property p : m_properties) {
            if (p == property) {
                return true;
            }
        }

        return false;
    }


    public void addModelListener(ModelListener listener) {
        if (null == listener) {
            throw new IllegalArgumentException("listener must not be null");
        }

        m_listeners.add(listener);
    }


    public void addProperty(Property property) {
        if (null == property) {
            throw new IllegalArgumentException("property must not be null");
        }

        if (getProperty(property.getName()) != null) {
            throw new IllegalArgumentException("duplicate property: " +
                    property.getName());
        }

        m_properties.add(property);

        notifyListeners();
    }

    public void addProperties(Property... properties) {
        // Arrays.asList(properties).forEach(this::addProperty); // java source 1.8
        for(Property p: properties) {
        	this.addProperty(p);
        }
    }

    public void addAction(Action action) {
        if (null == action) {
            throw new IllegalArgumentException("action must not be null");
        }

        if (getProperty(action.getName()) != null) {
            throw new IllegalArgumentException("duplicate action: " +
                    action.getName());
        }

        m_actions.add(action);

        notifyListeners();
    }


    public void addActions(Action... actions) {
        // Arrays.asList(actions).forEach(this::addAction);  // java source 1.8
        for(Action a: actions) {
        	this.addAction(a);
        }
    }

    public void addEvent(Event event) {
        if (null == event) {
            throw new IllegalArgumentException("Event must not be null");
        }

        if (getProperty(event.getName()) != null) {
            throw new IllegalArgumentException("duplicate action: " +
            		event.getName());
        }

        m_events.add(event);

        notifyListeners();
    }
    
    
    private void notifyListeners() {
        for (ModelListener listener : m_listeners) {
            listener.onChange(this);
        }
    }

    public boolean isProtected() {
        return protection;
    }

    public Thing setProtection(boolean protection) {
        this.protection = protection;
        return this;
    }

    private boolean protection = false;

    private final List<Property> m_properties =
            new CopyOnWriteArrayList<>();


    private final List<Property> m_propertiesView =
            Collections.unmodifiableList(m_properties);
    
    private final List<Event> m_events =
            new CopyOnWriteArrayList<>();


    private final List<Event> m_eventsView =
            Collections.unmodifiableList(m_events);


    private final List<Action> m_actions =
            new CopyOnWriteArrayList<>();


    private final List<Action> m_actionsView =
            Collections.unmodifiableList(m_actions);


    private final List<ModelListener> m_listeners =
            new CopyOnWriteArrayList<>();

    public Action getAction(String actionName) {
        if (null == actionName) {
            throw new IllegalArgumentException("actionName must not be null");
        }

        for (Action action : m_actions) {
            if (action.getName().equals(actionName))
                return action;
        }

        return null;
    }
    
    public Event getEvent(String eventName) {
        if (null == eventName) {
            throw new IllegalArgumentException("eventName must not be null");
        }

        for (Event event : m_events) {
            if (event.getName().equals(eventName))
                return event;
        }

        return null;
    }
}
