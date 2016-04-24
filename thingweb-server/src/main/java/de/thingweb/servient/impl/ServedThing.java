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

package de.thingweb.servient.impl;

import de.thingweb.servient.ThingInterface;
import de.thingweb.thing.Action;
import de.thingweb.thing.Property;
import de.thingweb.thing.Thing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Johannes on 20.12.2015.
 */
public class ServedThing implements ThingInterface {

    protected final static Logger log = LoggerFactory.getLogger(ServedThing.class);

    /**
     * Sync object for {@link #m_stateSync}.
     */
    private final Object m_stateSync = new Object();
    private final Thing m_thingModel;
    private final StateContainer m_stateContainer;
    private Consumer<Object> m_propertyGetCallback;

    public ServedThing(Thing thing) {
        this.m_thingModel = thing;
        this.m_stateContainer = new StateContainer(thing);
    }

    public Thing getThingModel() {
        return m_thingModel;
    }

    @Override
    public void setProperty(Property property, Object value) {
        if (null == property) {
            throw new IllegalArgumentException("property must not be null");
        }
        if (!m_thingModel.isOwnerOf(property)) {
            throw new IllegalArgumentException(
                    "property does not belong to served thing");
        }

        synchronized (m_stateSync) {
            m_stateContainer.setProperty(property, value);
            m_stateContainer.getUpdateHandlers(property)
                    .parallelStream()
                    .forEach(handler -> handler.accept(value));

            property.setChanged();
        }
    }

    @Override
    public void setProperty(String propertyName, Object value) {
        Property prop = m_thingModel.getProperty(propertyName);

        if (null == prop) {
            throw new IllegalArgumentException("no such property: " +
                    propertyName);
        }

        setProperty(prop, value);
    }

    @Override
    public Object getProperty(Property property) {
        if (null == property) {
            throw new IllegalArgumentException("property must not be null");
        }
        if (!m_thingModel.isOwnerOf(property)) {
            throw new IllegalArgumentException(
                    "property does not belong to served thing");
        }

        if(m_propertyGetCallback != null)
        	m_propertyGetCallback.accept(property);
        
        synchronized (m_stateSync) {
            return m_stateContainer.getProperty(property);
        }

    }

    @Override
    public Object getProperty(String propertyName) {
        Property prop = m_thingModel.getProperty(propertyName);

        if (null == prop) {
            throw new IllegalArgumentException("no such property: " +
                    propertyName);
        }

        return getProperty(prop);
    }

    @Override
    public Object invokeAction(String actionName, Object parameter) {
        Action action = m_thingModel.getAction(actionName);
        if (action == null) {
            log.warn("onInvoke for actionName '{}' not found in thing model", actionName);
            throw new IllegalArgumentException(actionName);
        } else {
            return invokeAction(action, parameter);
        }
    }

    @Override
    public Object invokeAction(Action action, Object parameter) {
        Function<?, ?> handler = m_stateContainer.getHandler(action);

        Function<Object, Object> objectHandler = (Function<Object, Object>) handler;
        Object result = objectHandler.apply(parameter);

        return result;
    }

    @Override
    public void onPropertyUpdate(String propertyName, Consumer<Object> callback) {
        Property property = m_thingModel.getProperty(propertyName);
        if (property == null) {
            log.warn("property {} not found in thing {}", propertyName, m_thingModel.getName());
            throw new IllegalArgumentException(propertyName);
        } else {
            m_stateContainer.addUpdateHandler(property, callback);
        }
    }

    @Override
    public void onUpdate(String propertyName, Consumer<Object> callback) {
        onPropertyUpdate(propertyName,callback);
    }

    @Override
    public void onPropertyRead(Consumer<Object> callback) {
    	m_propertyGetCallback = callback;
    }
    
    @Override
    public void onInvoke(String actionName, Function<Object, Object> callback) {
        onActionInvoke(actionName,callback);
    }

    //TODO overloads for void
    @Override
    public void onActionInvoke(String actionName, Function<Object, Object> callback) {
        Action action = m_thingModel.getAction(actionName);
        if (action == null) {
            log.warn("onInvoke for actionName '" + actionName + "' not found in thing model");
        } else {
            m_stateContainer.addHandler(action, callback);
        }
    }

    @Override
    public String getName() {
        return m_thingModel.getName();
    }
    
    @Override
    public List<String> getURIs(){
    	if(m_thingModel.getMetadata().contains("hrefs"))
    		return m_thingModel.getMetadata().getAll("hrefs");
    	else
    		return null;
    }
    
 // TODO This should perhaps be a generic add interaction. But wait and watch how proposals develop.
    //Taking this out, see Thing.addProperty
    public void addProperty(Property prop){
    	m_thingModel.addProperty(prop);
    	m_stateContainer.updateHandlers();
    }
}
