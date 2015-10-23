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

import java.util.List;

import de.webthing.client.Client;
import de.webthing.desc.pojo.ActionDescription;
import de.webthing.desc.pojo.EventDescription;
import de.webthing.desc.pojo.PropertyDescription;

public abstract class AbstractClientImpl implements Client {
	
	// private static final Logger log = LoggerFactory.getLogger(AbstractClientImpl.class);
	
	final List<PropertyDescription> properties;
	final List<ActionDescription> actions;
	final List<EventDescription> events;
	
	/** e.g., http://www.example.com:80/ledlamp or coap://localhost:5683/thingsMyLED */
	final String uri;

	final String URI_PART_PROPERTIES = "/properties/";
	final String URI_PART_ACTIONS = "/actions/";
	
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
