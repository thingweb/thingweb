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

package de.webthing.things;

import de.webthing.thing.Action;
import de.webthing.thing.Property;
import de.webthing.thing.Thing;

public final class WotGreeter {

	public static Thing newInstance(String name) {
		Thing thing = new Thing(name);
		
		thing.addProperty(Property.getBuilder("message").build());
		thing.addProperty(Property.getBuilder("customMessage").setWriteable(true).build());
		thing.addProperty(Property.getBuilder("secretMessage").setReadable(false).setWriteable(true).build());

		thing.addAction(Action.getBuilder("selftest").build());

		return thing;
	}
	
	
	private WotGreeter() {
		/* pure static class */
	}
}
