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

import de.thingweb.client.Client;
import de.thingweb.desc.pojo.ActionDescription;
import de.thingweb.desc.pojo.EventDescription;
import de.thingweb.desc.pojo.PropertyDescription;

import java.util.List;

public abstract class AbstractClientImpl implements Client {
	
	// private static final Logger log = LoggerFactory.getLogger(AbstractClientImpl.class);
	
	final List<PropertyDescription> properties;
	final List<ActionDescription> actions;
	final List<EventDescription> events;
	
	/** e.g., http://www.example.com:80/ledlamp or coap://localhost:5683/things/MyLED */
	final String uri;

	final String URI_PART_PROPERTIES = "/";
	//final String URI_PART_PROPERTIES = "/properties/";
	final String URI_PART_ACTIONS = "/";
	//final String URI_PART_ACTIONS = "/actions/";
	
	// TODO remove if settled
	final boolean useValueStringInGetAndPutUrl = true;

	public AbstractClientImpl(String uri, List<PropertyDescription> properties, List<ActionDescription> actions, List<EventDescription> events) {
		this.uri = uri;
		this.properties = properties;
		this.actions = actions;
		this.events = events;
	}
	
	public String getUsedProtocolURI() {
		return this.uri;
	}
	
	public List<PropertyDescription> getProperties() {
		return properties;
	}
	
	public List<ActionDescription> getActions() {
		return actions;
	}
	
	public List<EventDescription> getEvents() {
		return events;
	}

}
