package de.webthing.binding.coap;

import de.webthing.binding.Binding;
import de.webthing.binding.GrantAllTokenVerifier;
import de.webthing.binding.RESTListener;
import de.webthing.binding.ResourceBuilder;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

import java.util.Collections;
import java.util.List;

import static org.eclipse.californium.core.coap.MediaTypeRegistry.TEXT_PLAIN;


public class CoapBinding implements Binding {

	private CoapServer m_coapServer;
	private GrantAllTokenVerifier m_tokenVerfier;

	@Override
	public void initialize() {
		m_coapServer = new CoapServer();
		m_tokenVerfier = new GrantAllTokenVerifier();
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
						if(!authorize(exchange)) {
							exchange.respond(ResponseCode.UNAUTHORIZED);
							return;
						}
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
						if(!authorize(exchange)) {
							exchange.respond(ResponseCode.UNAUTHORIZED);
							return;
						}
						try {
							restListener.onPut(exchange.getRequestText().getBytes());
							exchange.respond(ResponseCode.CHANGED);
						}
						catch (UnsupportedOperationException e) {
							exchange.respond(ResponseCode.METHOD_NOT_ALLOWED);
						}
						catch (IllegalArgumentException e) {
							exchange.respond(ResponseCode.BAD_REQUEST);
						}

					}

					@Override
					public void handlePOST(CoapExchange exchange) {
						if(!authorize(exchange)) {
							exchange.respond(ResponseCode.UNAUTHORIZED);
							return;
						}
						try {
							byte[] resp = restListener.onPost(exchange.getRequestText().getBytes());
							//TODO: add Location Option to response
							exchange.respond(ResponseCode.CREATED, resp, TEXT_PLAIN);
						}
						catch (UnsupportedOperationException e) {
							exchange.respond(ResponseCode.METHOD_NOT_ALLOWED);
						}
						catch (IllegalArgumentException e) {
							exchange.respond(ResponseCode.BAD_REQUEST);
						}

					}

					@Override
					public void handleDELETE(CoapExchange exchange) {
						if(!authorize(exchange)) {
							exchange.respond(ResponseCode.UNAUTHORIZED);
							return;
						}
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

	//TODO set this right
	private static int TOKEN_BEARER_OPTION_NUMBER=42;

	private boolean authorize(CoapExchange exchange) {
		String jwt = null;
		List<Option> options = exchange.getRequestOptions().asSortedList();
		int idx = Collections.binarySearch(options, new Option(TOKEN_BEARER_OPTION_NUMBER));

		if(idx >= 0) {
			jwt = options.get(idx).getStringValue();
		} else {
			//no token, jwt == null
		}

		return m_tokenVerfier.isAuthorized(jwt);
	}

	@Override
	public void start() {
		m_coapServer.start();
	}
}
