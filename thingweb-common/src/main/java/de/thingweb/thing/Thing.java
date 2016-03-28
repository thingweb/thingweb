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

import de.thingweb.desc.pojo.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;


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
    private ThingDescription m_td;

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

        List<InteractionDescription> interactions = new ArrayList<>();
        List<String> protocols = new ArrayList<>();
        //TODO hardcoded JSON for now, retrieve list from ContentHelper
        List<String> encodings = Arrays.asList("JSON");

        Metadata metas = new Metadata(name, protocols,encodings, null);
        m_td = new ThingDescription(metas,interactions);
    }

    public Thing(ThingDescription desc) {
        this(desc.getMetadata().getName());
        m_td = desc;
        // TODO check support for HTTP and/or CoAP
        for (InteractionDescription i : desc.getInteractions()) {
            if (i instanceof PropertyDescription) {
                PropertyDescription pd = (PropertyDescription) i;
                Property p = new Property(pd);
                m_properties.add(p);
            } else if (i instanceof ActionDescription) {
                ActionDescription ad = (ActionDescription) i;
                Action a = Action.getBuilder(i.getName())
                        .setInputType(ad.getInputType())
                        .setOutputType(ad.getOutputType())
                        .build();
                m_actions.add(a);
            }
        }
    }

    public void addProperty(PropertyDescription pd){
        Property p = new Property(pd);
        m_properties.add(p);
        m_td.getInteractions().add(pd);
    }
    
    public void addInteractions(List<InteractionDescription> interactionDescriptions) {
        m_td.getInteractions().addAll(interactionDescriptions);
    }

    public String getName() {
        return m_name;
    }

    public ThingDescription getThingDescription() {
        return m_td;
    }


    public Collection<Property> getProperties() {
        return m_propertiesView;
    }


    public Collection<Action> getActions() {
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
            if (property.getDescription().getName().equals(propertyName)) {
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

        if (getProperty(property.getDescription().getName()) != null) {
            throw new IllegalArgumentException("duplicate property: " +
                    property.getDescription().getName());
        }

        m_properties.add(property);

        PropertyDescription pdesc = property.getDescription();
        m_td.getInteractions().add(pdesc);

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

        ActionDescription adesc = new ActionDescription(
                action.getName(),
                action.getInputType(),
                action.getOutputType()
        );
        m_td.getInteractions().add(adesc);

        notifyListeners();
    }


    public void addActions(Action... actions) {
        // Arrays.asList(actions).forEach(this::addAction);  // java source 1.8
        for(Action a: actions) {
        	this.addAction(a);
        }
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

    private final Collection<Property> m_properties =
            new CopyOnWriteArrayList<>();


    private final Collection<Property> m_propertiesView =
            Collections.unmodifiableCollection(m_properties);


    private final Collection<Action> m_actions =
            new CopyOnWriteArrayList<>();


    private final Collection<Action> m_actionsView =
            Collections.unmodifiableCollection(m_actions);


    private final Collection<ModelListener> m_listeners =
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
}
