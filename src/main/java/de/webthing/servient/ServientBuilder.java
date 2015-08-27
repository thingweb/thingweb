package de.webthing.servient;

import de.openwot.thing.binding.coap.CoapBinding;
import de.openwot.thing.binding.http.HttpBinding;
import de.webthing.servient.impl.MultiBindingThingServer;
import de.webthing.thing.Thing;


public final class ServientBuilder {
	/**
	 * Creates a new ThingServer for the specified thing model.
	 * 
	 * @param thing the thing model, must not be null
	 * @return the server, never null
	 */
	public static ThingServer newThingServer(Thing thing) {
		return new MultiBindingThingServer(thing, 
				m_coapBinding.getResourceBuilder(),
				m_httpBinding.getResourceBuilder()
				);
	}
	
	
	public static void initialize() throws Exception {
		m_coapBinding.initialize();
		m_httpBinding.initialize();
	}
	
	
	public static void start() throws Exception {
		m_coapBinding.start();
		m_httpBinding.start();
	}
	
	
	private ServientBuilder() {
		/* pure static class */
	}
	
	
	private static final CoapBinding m_coapBinding = new CoapBinding();
	
	
	private static final HttpBinding m_httpBinding = new HttpBinding();
}
