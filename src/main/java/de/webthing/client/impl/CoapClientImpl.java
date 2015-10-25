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

package de.webthing.client.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.webthing.binding.coap.WotCoapResource;
import de.webthing.client.Callback;
import de.webthing.desc.pojo.ActionDescription;
import de.webthing.desc.pojo.EventDescription;
import de.webthing.desc.pojo.PropertyDescription;
import de.webthing.desc.pojo.Protocol;
import de.webthing.thing.Content;

public class CoapClientImpl extends AbstractClientImpl {
	
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(CoapClientImpl.class);
	
	Map<String, CoapObserveRelation> observes = new HashMap<>();
	
	public CoapClientImpl(Protocol prot, List<PropertyDescription> properties, List<ActionDescription> actions, List<EventDescription> events) {
		super(prot.getUri(), properties, actions, events);
	}
	
	
	public void put(String propertyName, Content propertyValue, Callback callback) {
		doCoapPut(propertyName, propertyValue, callback, true);
	}
	
	
	protected void doCoapPut(String name, Content value, Callback callback, boolean isPut) {
		String uriPart = URI_PART_PROPERTIES;
		CoapClient coap = new CoapClient(uri + uriPart + name);
		coap.put(new CoapHandler() {

			@Override
			public void onLoad(CoapResponse response) {
				Content content = new Content(response.getPayload(), WotCoapResource.getMediaType(response.getOptions()));
					callback.onPut(name, content);
			}

			@Override
			public void onError() {
					callback.onPutError(name);
			}
		}, value.getContent(), WotCoapResource.getCoapContentFormat(value.getMediaType()));
	}

	protected void doCoapPost(String name, Content value, Callback callback) {
		final String uriPart = URI_PART_ACTIONS;
		CoapClient coap = new CoapClient(uri + uriPart + name);
		coap.post(new CoapHandler() {

			@Override
			public void onLoad(CoapResponse response) {
				Content content = new Content(response.getPayload(), WotCoapResource.getMediaType(response.getOptions()));
				callback.onAction(name, content);
			}

			@Override
			public void onError() {
				callback.onActionError(name);
			}
		}, value.getContent(), WotCoapResource.getCoapContentFormat(value.getMediaType()));
	}


	public void get(String propertyName, Callback callback) {
		CoapClient coap = new CoapClient(uri + URI_PART_PROPERTIES + propertyName);

		// asynchronous
		coap.get(new CoapHandler() {
			@Override
			public void onLoad(CoapResponse response) {
				Content content = new Content(response.getPayload(), WotCoapResource.getMediaType(response.getOptions()));
				callback.onGet(propertyName, content);
			}

			@Override
			public void onError() {
				callback.onGetError(propertyName);
			}
		});
	}
	
	
	public void observe(String propertyName, Callback callback) {
		CoapClient coap = new CoapClient(uri + URI_PART_PROPERTIES + propertyName);
		// observing
		CoapObserveRelation relation = coap.observe(new CoapHandler() {
			@Override
			public void onLoad(CoapResponse response) {
				Content content = new Content(response.getPayload(), WotCoapResource.getMediaType(response.getOptions()));
				callback.onObserve(propertyName, content);
			}

			@Override
			public void onError() {
				callback.onObserveError(propertyName);
			}
		});
		
		observes.put(propertyName, relation);
	}
	
	public void observeRelease(String propertyName) {
		observes.remove(propertyName).proactiveCancel();
	}

	
	public void action(String actionName, Content actionValue, Callback callback) {
		doCoapPost(actionName, actionValue, callback);
	}

}
