package de.webthing.servient.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.logging.Logger;

import de.webthing.binding.ResourceBuilder;
import de.webthing.servient.Defines;
import de.webthing.servient.InteractionListener;
import de.webthing.servient.ThingServer;
import de.webthing.thing.Action;
import de.webthing.thing.Property;
import de.webthing.thing.Content;
import de.webthing.thing.Thing;


/**
 * {@link ThingServer} implementation capable of offering a Thing via multiple
 * bindings simultaneously.
 */
public class MultiBindingThingServer implements ThingServer {
	
	/** The logger. */
	private final static Logger log = Logger.getLogger(MultiBindingThingServer.class.getCanonicalName());
	
	public MultiBindingThingServer(Thing thingModel, 
			ResourceBuilder ... bindings) {
		
		if (null == thingModel) {
			throw new IllegalArgumentException("thingModel must not be null");
		}
		
		for (ResourceBuilder b : bindings) {
			m_bindings.add(b);
		}
		
		m_thingModel = thingModel;
		m_state = new StateContainer(m_thingModel);

		createBindings();

	}
	

	@Override
	public void setProperty(Property property, Content value) {
		if (null == property) {
			throw new IllegalArgumentException("property must not be null");
		}
		if (!m_thingModel.isOwnerOf(property)) {
			throw new IllegalArgumentException(
					"property does not belong to served thing");
		}
		
		synchronized (m_stateSync) {
			m_state.setProperty(property, value);
		}
	}


	@Override
	public void setProperty(String propertyName, Content value) {
		Property prop = m_thingModel.getProperty(propertyName);
		
		if (null == prop) {
			throw new IllegalArgumentException("no such property: " + 
					propertyName);
		}
		
		setProperty(prop, value);
	}
	
	
	@Override
	public Content getProperty(Property property) {
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
	public Content getProperty(String propertyName) {
		Property prop = m_thingModel.getProperty(propertyName);
		
		if (null == prop) {
			throw new IllegalArgumentException("no such property: " + 
					propertyName);
		}
		
		return getProperty(prop);
	}

	@Override
	public void onInvoke(String actionName, Function<Object, Object> callback) {
		Action action = m_thingModel.getAction(actionName);
		m_state.addHandler(action, callback);
	}

	@Override
	public void addInteractionListener(InteractionListener listener) {
		m_listeners.add(listener);
	}
	
	
	private void createBindings() {
		for (ResourceBuilder binding : m_bindings) {
			createBinding(binding);
		}
	}

		
	private void createBinding(ResourceBuilder resources) {
		// System.out.println("createBinding " + resources);
		for (Property property : m_thingModel.getProperties()) {
			String url = Defines.BASE_THING_URL + m_thingModel.getName() +
					Defines.REL_PROPERTY_URL + property.getName();
			resources.newResource(url, new PropertyListener(this, property));
		}

		for (Action action : m_thingModel.getActions()) {
			//TODO optimize by preconstructing strings and using format
			String url = Defines.BASE_THING_URL + m_thingModel.getName() +
					Defines.REL_ACTION_URL + action.getName();
			resources.newResource(url, new ActionListener(m_state, action));
		}
	}
	
	
	Content readProperty(Property property) {
		for (InteractionListener listener : m_listeners) {
			listener.onReadProperty(this);
		}
		
		return getProperty(property);
	}
	
	
	void writeProperty(Property property, Content value) {
		setProperty(property, value);
		
		for (InteractionListener listener : m_listeners) {
			listener.onWriteProperty(this);
		}
	}

	/**
	 * Sync object for {@link #m_stateSync}.
	 */
	private final Object m_stateSync = new Object();
	
	
	private final StateContainer m_state;
	

	private final Collection<InteractionListener> m_listeners = 
			new CopyOnWriteArrayList<>();
	
	
	private final Collection<ResourceBuilder> m_bindings = new ArrayList<>(); 

	private final Thing m_thingModel;

}
