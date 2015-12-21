package de.thingweb.servient.impl;

import de.thingweb.servient.ThingInterface;
import de.thingweb.thing.Action;
import de.thingweb.thing.Property;
import de.thingweb.thing.Thing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final StateContainer m_state;

    public ServedThing(Thing thing) {
        this.m_thingModel = thing;
        this.m_state = new StateContainer(thing);
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
            m_state.setProperty(property, value);
            m_state.getUpdateHandlers(property)
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

        synchronized (m_stateSync) {
            return m_state.getProperty(property);
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
        Function<?, ?> handler = m_state.getHandler(action);

        Function<Object, Object> objectHandler = (Function<Object, Object>) handler;
        Object result = objectHandler.apply(parameter);

        return result;
    }

    @Override
    public void onUpdate(String propertyName, Consumer<Object> callback) {
        Property property = m_thingModel.getProperty(propertyName);
        if (property == null) {
            log.warn("property {} not found in thing {}", propertyName, m_thingModel.getName());
            throw new IllegalArgumentException(propertyName);
        } else {
            m_state.addUpdateHandler(property, callback);
        }
    }

    @Override
    public void onInvoke(String actionName, Function<Object, Object> callback) {
        Action action = m_thingModel.getAction(actionName);
        if (action == null) {
            log.warn("onInvoke for actionName '" + actionName + "' not found in thing model");
        } else {
            m_state.addHandler(action, callback);
        }
    }

}
