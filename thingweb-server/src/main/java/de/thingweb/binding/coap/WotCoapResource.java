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

package de.thingweb.binding.coap;

import de.thingweb.binding.RESTListener;
import de.thingweb.security.TokenExpiredException;
import de.thingweb.security.UnauthorizedException;
import de.thingweb.servient.impl.PropertyListener;
import de.thingweb.thing.Content;
import de.thingweb.thing.MediaType;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.network.Exchange;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.jose4j.json.internal.json_simple.JSONObject;

import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

/**
 * Created by Johannes on 05.10.2015.
 */
public class WotCoapResource extends CoapResource implements  Observer{
    private final RESTListener m_restListener;

    public WotCoapResource(String name, RESTListener restListener) {
        super(name);
        this.m_restListener = restListener;
        restListener.addObserver(this);
        if(restListener instanceof PropertyListener && ((PropertyListener)restListener).isObservable()){
        	this.setObservable(true);
        }
    }

    //TODO pull up into coap-spefic helper
    public static int getCoapContentFormat(MediaType mediaType) {
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
    
    public static MediaType getMediaType(OptionSet os) {
        MediaType mt;
        if(os.getContentFormat() == -1) {
        	// undefined
        	mt = MediaType.APPLICATION_JSON;
        } else {
        	String mediaType = MediaTypeRegistry.toString(os.getContentFormat());
        	mt = MediaType.getMediaType(mediaType);
        }
        return mt;
    }

    @Override
    public void handleRequest(Exchange exchange) {
        final CoapExchange coapExchange = new CoapExchange(exchange, this);       
        try {
            final CoAP.Code code = exchange.getRequest().getCode();
            authorize(coapExchange, code.toString());
            switch (code) {
                case GET:	handleGET(coapExchange); break;
                case POST:	handlePOST(coapExchange); break;
                case PUT:	handlePUT(coapExchange); break;
                case DELETE: handleDELETE(coapExchange); break;
            }
        } catch (UnauthorizedException e) {
            coapExchange.respond(CoAP.ResponseCode.UNAUTHORIZED);
        } catch (TokenExpiredException e) {
            coapExchange.respond(CoAP.ResponseCode.FORBIDDEN);
        }
    }

    private void authorize(CoapExchange exchange, String method) throws UnauthorizedException, TokenExpiredException {
        if(m_restListener.hasProtection()) {
            Optional<Option> tokenOption = exchange.getRequestOptions().asSortedList()
                    .parallelStream()
                    .filter(option -> option.getNumber() == 65000)
                    .findFirst();

            if (tokenOption.isPresent()) {
                String auth = tokenOption.get().getStringValue();
                String jwt = null;
                if (auth != null) {
                    if (auth.startsWith("Bearer ")) {
                        jwt = auth.substring("Bearer ".length());
                    }
                }
                m_restListener.validate(method.toUpperCase(), this.getURI(), jwt);
            } else {
                throw new UnauthorizedException("No security token found");
            }
        }
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        try {
            boolean hasObs = exchange.getRequestOptions().hasObserve();
            System.out.println("Request says it has obs=" + hasObs);
            if(hasObs){
            	Integer obsVal = exchange.getRequestOptions().getObserve();
            	System.out.println("Request says it has obsval =" + obsVal) ;
            	if(m_restListener instanceof PropertyListener)
            		((PropertyListener)m_restListener).setClientObservationState(obsVal == 0);
            }
            
            Content response = m_restListener.onGet();
        	int contentFormat = getCoapContentFormat(response.getMediaType());
        	exchange.respond(CoAP.ResponseCode.CONTENT, response.getContent(), contentFormat);
        } catch (UnsupportedOperationException e) {
            exchange.respond(CoAP.ResponseCode.METHOD_NOT_ALLOWED);
        } catch (Exception e) {
        	JSONObject errorObject = new JSONObject();
        	errorObject.put("errorMessage", e.getMessage());
        	String responsePayload = errorObject.toJSONString();
        	//TODO Media type be must got from rest listener..
        	exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR, responsePayload, MediaTypeRegistry.APPLICATION_JSON);
        }
    }

    @Override
    public void handlePUT(CoapExchange exchange) {
        try {
            // e.g., "Content-Format":"application/exi", "Accept":"application/xml"
            MediaType mt = getMediaType(exchange.getRequestOptions());
            m_restListener.onPut(new Content(exchange.getRequestPayload(), mt));
            exchange.respond(CoAP.ResponseCode.CHANGED);
        } catch (UnsupportedOperationException e) {
            exchange.respond(CoAP.ResponseCode.METHOD_NOT_ALLOWED);
        } catch (IllegalArgumentException e) {
            exchange.respond(CoAP.ResponseCode.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        try {
            byte[] reqPayload = exchange.getRequestPayload();
            MediaType mt = getMediaType(exchange.getRequestOptions());
            Content request = new Content(reqPayload, mt);
        	Content response = m_restListener.onPost(request);
        	int contentFormat = getCoapContentFormat(response.getMediaType());
        	if(response.getLocationPath() != null)        		
        		exchange.setLocationPath(response.getLocationPath());
        	
        	CoAP.ResponseCode responseCode = CoAP.ResponseCode.CREATED;
        	
        	if(response.getResponseType() == Content.ResponseType.UPDATED)
        		responseCode = CoAP.ResponseCode.CHANGED;
        	else if(response.getResponseType() == Content.ResponseType.ERROR)
        		responseCode = CoAP.ResponseCode.NOT_ACCEPTABLE;
        	
        	exchange.respond(responseCode, response.getContent(), contentFormat);
        } catch (UnsupportedOperationException e) {
            exchange.respond(CoAP.ResponseCode.METHOD_NOT_ALLOWED);
        } catch (IllegalArgumentException e) {
            exchange.respond(CoAP.ResponseCode.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public void handleDELETE(CoapExchange exchange) {
        try {
            m_restListener.onDelete();
            exchange.respond(CoAP.ResponseCode.DELETED);
        } catch (UnsupportedOperationException e) {
            exchange.respond(CoAP.ResponseCode.METHOD_NOT_ALLOWED);
        } catch (Exception e) {
            exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }    }

    @Override
    public void update(Observable o, Object arg) {
        LOGGER.info("change detected: " + o + " to " + arg);
        this.changed();
    }
}
