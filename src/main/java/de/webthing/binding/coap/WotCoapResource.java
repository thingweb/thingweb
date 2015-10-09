package de.webthing.binding.coap;

import de.webthing.binding.RESTListener;
import de.webthing.binding.auth.TokenVerifier;
import de.webthing.thing.MediaType;
import de.webthing.thing.Content;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.util.Collections;
import java.util.List;

/**
 * Created by Johannes on 05.10.2015.
 */
public class WotCoapResource extends CoapResource {
    private final RESTListener m_restListener;
    private TokenVerifier m_tokenVerifier;

    public WotCoapResource(String name, RESTListener restListener, TokenVerifier tokenVerifier) {
        super(name);
        this.m_restListener = restListener;
        this.m_tokenVerifier = tokenVerifier;
    }

    //TODO set this right
    private static int TOKEN_BEARER_OPTION_NUMBER = 42;

    private boolean authorize(CoapExchange exchange) {
        String jwt = null;
        List<Option> options = exchange.getRequestOptions().asSortedList();
        int idx = Collections.binarySearch(options, new Option(TOKEN_BEARER_OPTION_NUMBER));

        if (idx >= 0) {
            jwt = options.get(idx).getStringValue();
        } else {
            //no token, jwt == null
        }

        return m_tokenVerifier.isAuthorized(jwt);
    }
    
    
    private static int getCoapContentFormat(MediaType mediaType) {
    	int contentFormat;
    	switch(mediaType) {
    	case TEXT_PLAIN:
    		contentFormat = MediaTypeRegistry.TEXT_PLAIN;
    		break;
    	case APPLICATION_XML:
    		contentFormat = MediaTypeRegistry.APPLICATION_XML;
    		break;
    	case APPLICATION_EXI:
    		contentFormat = MediaTypeRegistry.APPLICATION_EXI;
    		break;
    	case APPLICATION_JSON:
    		contentFormat = MediaTypeRegistry.APPLICATION_JSON;
    		break;
    	default:
    		// TODO how to deal best?
    		contentFormat = MediaTypeRegistry.UNDEFINED;
    	}
    	return contentFormat;
    }
    
    @Override
    public void handleGET(CoapExchange exchange) {
        if (!authorize(exchange)) {
            exchange.respond(CoAP.ResponseCode.UNAUTHORIZED);
            return;
        }
        try {
            Content response = m_restListener.onGet();
        	int contentFormat = getCoapContentFormat(response.getMediaType());
        	exchange.respond(CoAP.ResponseCode.CONTENT, response.getContent(), contentFormat);
            
        } catch (UnsupportedOperationException e) {
            exchange.respond(CoAP.ResponseCode.METHOD_NOT_ALLOWED);
        }
    }

    @Override
    public void handlePUT(CoapExchange exchange) {
        if (!authorize(exchange)) {
            exchange.respond(CoAP.ResponseCode.UNAUTHORIZED);
            return;
        }
        try {
        	// e.g., "Content-Format":"application/exi", "Accept":"application/xml"
        	OptionSet os = exchange.getRequestOptions();
            MediaType mt;
            if(os.getContentFormat() == -1) {
            	// undefined
            	mt = MediaType.UNDEFINED;
            } else {
            	String mediaType = MediaTypeRegistry.toString(os.getContentFormat());
            	mt = MediaType.getMediaType(mediaType);
            }
            m_restListener.onPut(new Content(exchange.getRequestPayload(), mt));
            exchange.respond(CoAP.ResponseCode.CHANGED);
        } catch (UnsupportedOperationException e) {
            exchange.respond(CoAP.ResponseCode.METHOD_NOT_ALLOWED);
        } catch (IllegalArgumentException e) {
            exchange.respond(CoAP.ResponseCode.BAD_REQUEST);
        }

    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        if (!authorize(exchange)) {
            exchange.respond(CoAP.ResponseCode.UNAUTHORIZED);
            return;
        }
        try {
        	byte[] reqPayload = exchange.getRequestPayload();
        	System.out.println("RequestOptions: " + exchange.getRequestOptions());
        	Content request = new Content(reqPayload, MediaType.TEXT_PLAIN);
        	Content response = m_restListener.onPost(request);
        	int contentFormat = getCoapContentFormat(response.getMediaType());
        	exchange.respond(CoAP.ResponseCode.CREATED, response.getContent(), contentFormat);
        	 
            // byte[] resp = m_restListener.onPost(exchange.getRequestText().getBytes());
            //TODO: add Location Option to response
            // exchange.respond(CoAP.ResponseCode.CREATED, resp, TEXT_PLAIN);
        } catch (UnsupportedOperationException e) {
            exchange.respond(CoAP.ResponseCode.METHOD_NOT_ALLOWED);
        } catch (IllegalArgumentException e) {
            exchange.respond(CoAP.ResponseCode.BAD_REQUEST);
        }

    }

    @Override
    public void handleDELETE(CoapExchange exchange) {
        if (!authorize(exchange)) {
            exchange.respond(CoAP.ResponseCode.UNAUTHORIZED);
            return;
        }
        try {
            m_restListener.onDelete();
            exchange.respond(CoAP.ResponseCode.DELETED);
        } catch (UnsupportedOperationException e) {
            exchange.respond(CoAP.ResponseCode.METHOD_NOT_ALLOWED);
        }
    }
}
