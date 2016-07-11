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

package de.thingweb.client.impl;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.MessageObserver;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.thingweb.client.Callback;
import de.thingweb.thing.Content;
import de.thingweb.thing.MediaType;
import de.thingweb.thing.Thing;
import de.thingweb.util.encoding.ContentHelper;

public class CoapClientImpl extends AbstractClientImpl {
	
	private static final Logger log = LoggerFactory.getLogger(CoapClientImpl.class);
	
	final int SECURITY_TOKEN_NUMBER = 65000;
	final String SECURITY_BEARER_STRING = "Bearer ";
	
	Map<String, ObserveRelation> observes = new HashMap<>();
	
	public CoapClientImpl(Thing thing, int uriIndex) {
		super(thing, uriIndex);
	}
	
	
	public void put(String propertyName, Content propertyValue, Callback callback) {
		put(propertyName, propertyValue, callback, null);
	}
	
	public void put(String propertyName, Content propertyValue, Callback callback, String securityAsToken) {
		 put(propertyName, propertyValue, callback, securityAsToken, useValueInUrlFirst);
	}
	
	protected void put(final String propertyName, final Content propertyValue, final Callback callback, final String securityAsToken, final boolean useValue) {
		
		final String curi = this.getThing().resolvePropertyUri(propertyName, index);
		
		CoapClient coap = new CoapClient(curi);
		
		log.info("CoAP PUT " + coap.getURI() + " (Security=" + securityAsToken + ") with: " + new String(propertyValue.getContent()));
		
		Request request = Request.newPut();
		request.setPayload(propertyValue.getContent());
		request.getOptions().setContentFormat(getCoapContentFormat(propertyValue.getMediaType()));
		
		if(securityAsToken != null) {
			Option tokenOption = new Option(SECURITY_TOKEN_NUMBER, (SECURITY_BEARER_STRING + securityAsToken));
			request.getOptions().addOption(tokenOption);			
		}
		
		// asynchronous
		coap.advanced(new CoapHandler() {
			@Override
			public void onLoad(CoapResponse response) {
				if(ResponseCode.isSuccess(response.getCode()) ) {
					Content content = new Content(response.getPayload(), getMediaType(response.getOptions()));
					callback.onPut(propertyName, content);
				} else {
					error(response.getCode().name());
				}
			}

			void error(String message) {
				callback.onPutError(propertyName, message);
			}
			
			@Override
			public void onError() {
				error("Network error");
			}
		}, request);	
	}

	public void get(String propertyName, Callback callback) {
		get(propertyName, callback, null);
	}
	
	public void get(String propertyName, Callback callback, String securityAsToken) {
		get(propertyName, callback, securityAsToken, useValueInUrlFirst);
	}
	
	protected void get(final String propertyName, final Callback callback, final String securityAsToken, final boolean useValue) {
		
		final String curi = this.getThing().resolvePropertyUri(propertyName, index);
		
		CoapClient coap = new CoapClient(curi);
		
		log.info("CoAP get " + coap.getURI() + " (Security=" + securityAsToken + ")");
		
		Request request = Request.newGet();
		if(securityAsToken != null) {
			Option tokenOption = new Option(SECURITY_TOKEN_NUMBER, (SECURITY_BEARER_STRING + securityAsToken));
			request.getOptions().addOption(tokenOption);			
		}
		
		// asynchronous
		coap.advanced(new CoapHandler() {
			@Override
			public void onLoad(CoapResponse response) {
				if(ResponseCode.isSuccess(response.getCode()) ) {
					Content content = new Content(response.getPayload(), getMediaType(response.getOptions()));
					callback.onGet(propertyName, content);
				} else {
					error();
				}
			}
			
			void error() {
				if(useValueInUrlFirst == useValue) {
					// try the other URL form as well before reporting error
					log.warn("The GET uri='" + curi + "' was not successull. Try with /value form next: " + !useValue);
					get(propertyName, callback, securityAsToken, !useValue);
				} else {
					callback.onGetError(propertyName);
				}
			}

			@Override
			public void onError() {
				error();
			}
		}, request);
	}
	
	
	public void observe(String propertyName, Callback callback) {
		observe(propertyName, callback, null);
	}
	
	public void observe(String propertyName, Callback callback, String securityAsToken) {
		observe(propertyName, callback, securityAsToken, useValueInUrlFirst);
	}
	
	protected void observe(final String propertyName, final Callback callback, final String securityAsToken, final boolean useValue) {
		
		final String curi = this.getThing().resolvePropertyUri(propertyName, index);
		
		CoapClient coap = new CoapClient(curi);
		
		log.info("CoAP observe " + coap.getURI() + " (Security=" + securityAsToken + ")");
		
		Request request = Request.newGet().setObserve();
		if(securityAsToken != null) {
			Option tokenOption = new Option(SECURITY_TOKEN_NUMBER, (SECURITY_BEARER_STRING + securityAsToken));
			request.getOptions().addOption(tokenOption);			
		}
		
		// asynchronous
		coap.advanced(new CoapHandler() {
			@Override
			public void onLoad(CoapResponse response) {
				if(ResponseCode.isSuccess(response.getCode()) ) {
					Content content = new Content(response.getPayload(), getMediaType(response.getOptions()));
					callback.onObserve(propertyName, content);
				} else {
					error();
				}
			}
			
			void error() {
				if(useValueInUrlFirst == useValue) {
					// try the other URL form as well before reporting error
					log.warn("The OBSERVE uri='" + curi + "' was not successull. Try with /value form next: " + !useValue);
					observe(propertyName, callback, securityAsToken, !useValue);
				} else {
					callback.onObserveError(propertyName);
				}
			}

			@Override
			public void onError() {
				error();
			}
		}, request);
		
		observes.put(propertyName, new ObserveRelation(request, coap));
	}
	
	class ObserveRelation {
		final Request request;
		final CoapClient coap;
		public ObserveRelation(Request request, CoapClient coap) {
			this.request = request;
			this.coap = coap;
		}
	}
	
	protected void proactiveCancel(ObserveRelation or) {
		Request request = or.request;
		
		Request cancel = Request.newGet();
		// copy options, but set Observe to cancel
		cancel.setOptions(request.getOptions());
		cancel.setObserveCancel();
		// use same Token
		cancel.setToken(request.getToken());
		cancel.setDestination(request.getDestination());
		cancel.setDestinationPort(request.getDestinationPort());
		// dispatch final response to the same message observers
		for (MessageObserver mo: request.getMessageObservers())
			cancel.addMessageObserver(mo);
		// endpoint.sendRequest(cancel);
		or.coap.advanced(cancel);
		// cancel old ongoing request
		request.cancel();
		// setCanceled(true);
	}
	
	
	public void observeRelease(String propertyName) {
		proactiveCancel(observes.remove(propertyName));
	}

	
	public void action(String actionName, Content actionValue, Callback callback) {
		action(actionName, actionValue, callback, null);
	}
	
	public void action(final String actionName, Content actionValue, final Callback callback, String securityAsToken) {

		final String curi = this.getThing().resolveActionUri(actionName, index);
		
		CoapClient coap = new CoapClient(curi);
		
		log.info("CoAP post " + coap.getURI() + " (Security=" + securityAsToken + ")");
		
		Request request = Request.newPost();
		request.setPayload(actionValue.getContent());
		request.getOptions().setContentFormat(getCoapContentFormat(actionValue.getMediaType()));
		
		if(securityAsToken != null) {
			Option tokenOption = new Option(SECURITY_TOKEN_NUMBER, (SECURITY_BEARER_STRING + securityAsToken));
			request.getOptions().addOption(tokenOption);			
		}
		
		// asynchronous
		coap.advanced(new CoapHandler() {
			@Override
			public void onLoad(CoapResponse response) {
				Content content = new Content(response.getPayload(), getMediaType(response.getOptions()));
				callback.onAction(actionName, content);
			}

			@Override
			public void onError() {
				callback.onActionError(actionName);
			}
		}, request);
	}


	public static MediaType getMediaType(OptionSet os) {
		MediaType mt;
		if (os.getContentFormat() == -1) {
			// undefined
			mt = MediaType.APPLICATION_JSON;
		} else {
			String mediaType = MediaTypeRegistry.toString(os.getContentFormat());
			mt = MediaType.getMediaType(mediaType);
		}
		return mt;
	}

	public static int getCoapContentFormat(MediaType mediaType) {
		int contentFormat;
		switch (mediaType) {
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
}
