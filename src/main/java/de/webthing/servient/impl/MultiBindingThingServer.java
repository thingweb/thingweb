package de.webthing.servient.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import de.webthing.binding.AbstractRESTListener;
import de.webthing.binding.ResourceBuilder;
import de.webthing.servient.Defines;
import de.webthing.servient.InteractionListener;
import de.webthing.servient.ThingServer;
import de.webthing.thing.Property;
import de.webthing.thing.Thing;


/**
 * {@link ThingServer} implementation capable of offering a Thing via multiple
 * bindings simultaneously.
 */
public class MultiBindingThingServer implements ThingServer {
	
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
	public void addInteractionListener(InteractionListener listener) {
		m_listeners.add(listener);
	}
	
	
	private void createBindings() {
		for (ResourceBuilder binding : m_bindings) {
			createBinding(binding);
		}
	}

		
	private void createBinding(ResourceBuilder resources) {
		/*
		 * FIXME: add support for mime types.
		 * The current implementation returns always text/plain via a String
		 * return value from RESTListener. Content type based serialization
		 * should be wedged in between here.
		 */
		
		for (Property property : m_thingModel.getProperties()) {
			String url = Defines.BASE_THING_URL + m_thingModel.getName() +
					Defines.REL_PROPERTY_URL + property.getName();
			
			resources.newResource(url, new AbstractRESTListener() {
				@Override
				public String onGet() {
					if (!property.isReadable()) {
						throw new UnsupportedOperationException();
					}
					
					return readProperty(property).toString();
				}

				@Override
				public void onPut(String data) {
					if (!property.isWriteable()) {
						throw new UnsupportedOperationException();
					}
					
					writeProperty(property, data);
				}
			});
		}
	}
	
	
	private Object readProperty(Property property) {
		for (InteractionListener listener : m_listeners) {
			listener.onReadProperty(this);
		}
		
		return getProperty(property);
	}
	
	
	private void writeProperty(Property property, Object value) {
		setProperty(property, value);
		
		for (InteractionListener listener : m_listeners) {
			listener.onReadProperty(this);
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
