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
import de.thingweb.thing.Thing;

public abstract class AbstractClientImpl implements Client {
	
  final Thing thing;
  
	/** e.g., http://www.example.com:80/ledlamp or coap://localhost:5683/things/MyLED */
	final String uri;

	final String URI_PART_PROPERTIES = "/";
	//final String URI_PART_PROPERTIES = "/properties/";
	final String URI_PART_ACTIONS = "/";
	//final String URI_PART_ACTIONS = "/actions/";
	
	/** Note: there has been different understanding whether to use /value in GET/PUT urls
	 * <p>Therefore we try both variants before reporting an error</p>
	 */
	final boolean useValueInUrlFirst = false;
	final String VALUE_STRING = "/value";

	public AbstractClientImpl(String uri, Thing thing) {
		this.thing = thing;
		this.uri = uri;
	}
	
	public String getUsedProtocolURI() {
		return this.uri;
	}
	
	@Override
	public Thing getThing() {
	  return thing;
	}

}
