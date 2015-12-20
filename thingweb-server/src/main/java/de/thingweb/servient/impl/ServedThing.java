package de.thingweb.servient.impl;

import de.thingweb.servient.InteractionListener;
import de.thingweb.servient.ThingInterface;
import de.thingweb.servient.ThingServer;
import de.thingweb.thing.Action;
import de.thingweb.thing.Property;
import de.thingweb.thing.Thing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
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
    private final Collection<InteractionListener> m_listeners =
            new CopyOnWriteArrayList<>();
    private StateContainer m_state;
    private Thing m_thingModel;

    public ServedThing(Thing thing) {

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
            for (InteractionListener listener : m_listeners) {
                listener.onWriteProperty(property.getName(), value, this);
            }
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

        for (InteractionListener listener : m_listeners) {
            listener.onReadProperty(property.getName(), this);
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

    }

    @Override
    public void onUpdate(String propertyName, Consumer<Object> callback) {
        this.addInteractionListener(new InteractionListener() {
            @Override
            public void onReadProperty(String propertyName, ThingServer thingServer) {

            }

            @Override
            public void onWriteProperty(String changedPropertyName, Object newValue, ThingServer thingServer) {
                if(changedPropertyName.equals(propertyName)) {
                    callback.accept(newValue);
                }
            }
        });
    }

    @Override
    public void onInvoke(String actionName, Function<Object, Object> callback) {
        Action action = m_thingModel.getAction(actionName);
        if(action == null) {
            log.warn("onInvoke for actionName '" + actionName + "' not found in thing model");
        } else {
            m_state.addHandler(action, callback);
        }
    }

    @Override
    public void addInteractionListener(InteractionListener listener) {
        m_listeners.add(listener);
    }
}
