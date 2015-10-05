package de.webthing.binding.coap;

import de.webthing.binding.Binding;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

import de.webthing.binding.RESTListener;
import de.webthing.binding.ResourceBuilder;

import static org.eclipse.californium.core.coap.MediaTypeRegistry.*;


public class CoapBinding implements Binding {

	private CoapServer m_coapServer;


	@Override
	public void initialize() {
		m_coapServer = new CoapServer();
	}

	@Override
	public ResourceBuilder getResourceBuilder() {
		return new ResourceBuilder() {
			@Override
			public void newResource(String url, RESTListener restListener) {
				String[] parts = url.split("/");
				
				Resource current = m_coapServer.getRoot();
				for (int i = 0; i < parts.length - 1; i++) {
					if (parts[i].isEmpty()) {
						continue;
					}
					
					Resource child = current.getChild(parts[i]);
					
					if (child == null) {
						child = new CoapResource(parts[i]);
						current.add(child);
					}
					
					current = child;
				}
				
				current.add(new CoapResource(parts[parts.length - 1]) {
					@Override
					public void handleGET(CoapExchange exchange) {
						try {
							byte[] response = restListener.onGet();
							
							exchange.respond(ResponseCode.CONTENT, response, TEXT_PLAIN);
						}
						catch (UnsupportedOperationException e) {
							exchange.respond(ResponseCode.METHOD_NOT_ALLOWED);
						}
					}

					@Override
					public void handlePUT(CoapExchange exchange) {
						try {
							restListener.onPut(exchange.getRequestText().getBytes());
							
							exchange.respond(ResponseCode.CHANGED);
						}
						catch (UnsupportedOperationException e) {
							exchange.respond(ResponseCode.METHOD_NOT_ALLOWED);
						}
					}

					@Override
					public void handlePOST(CoapExchange exchange) {
						try {
							byte[] resp = restListener.onPost(exchange.getRequestText().getBytes());
							//TODO: add Location Option to response
							exchange.respond(ResponseCode.CREATED, resp, TEXT_PLAIN);
						}
						catch (UnsupportedOperationException e) {
							exchange.respond(ResponseCode.METHOD_NOT_ALLOWED);
						}
					}

					@Override
					public void handleDELETE(CoapExchange exchange) {
						try {
							restListener.onDelete();

							exchange.respond(ResponseCode.DELETED);
						}
						catch (UnsupportedOperationException e) {
							exchange.respond(ResponseCode.METHOD_NOT_ALLOWED);
						}
					}
				});
			}
		};
	}
	
	@Override
	public void start() {
		m_coapServer.start();
	}
}
