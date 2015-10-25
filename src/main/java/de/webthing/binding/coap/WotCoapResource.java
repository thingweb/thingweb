/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Siemens AG and the thingweb community
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package de.webthing.binding.coap;

import de.webthing.binding.RESTListener;
import de.webthing.thing.Content;
import de.webthing.thing.MediaType;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Johannes on 05.10.2015.
 */
public class WotCoapResource extends CoapResource implements  Observer{
    private final RESTListener m_restListener;

    public WotCoapResource(String name, RESTListener restListener) {
        super(name);
        this.m_restListener = restListener;
        this.setObservable(true);
    }

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
        	mt = MediaType.UNDEFINED;
        } else {
        	String mediaType = MediaTypeRegistry.toString(os.getContentFormat());
        	mt = MediaType.getMediaType(mediaType);
        }
        return mt;
    }
    
    @Override
    public void handleGET(CoapExchange exchange) {
        try {
            Content response = m_restListener.onGet();
        	int contentFormat = getCoapContentFormat(response.getMediaType());
        	exchange.respond(CoAP.ResponseCode.CONTENT, response.getContent(), contentFormat);
        } catch (UnsupportedOperationException e) {
            exchange.respond(CoAP.ResponseCode.METHOD_NOT_ALLOWED);
        } catch (Exception e) {
            exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR, e.getMessage());
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
            exchange.respond(CoAP.ResponseCode.BAD_REQUEST);
        } catch (Exception e) {
            exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        try {
        	byte[] reqPayload = exchange.getRequestPayload();
        	System.out.println("RequestOptions: " + exchange.getRequestOptions());
            MediaType mt = getMediaType(exchange.getRequestOptions());
            Content request = new Content(reqPayload, mt);
        	Content response = m_restListener.onPost(request);
        	int contentFormat = getCoapContentFormat(response.getMediaType());

            //TODO: add Location Option to response
        	exchange.respond(CoAP.ResponseCode.CREATED, response.getContent(), contentFormat);
        } catch (UnsupportedOperationException e) {
            exchange.respond(CoAP.ResponseCode.METHOD_NOT_ALLOWED);
        } catch (IllegalArgumentException e) {
            exchange.respond(CoAP.ResponseCode.BAD_REQUEST);
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
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        this.changed();
    }
}
