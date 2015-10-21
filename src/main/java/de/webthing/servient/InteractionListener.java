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

package de.webthing.servient;


import de.webthing.thing.Content;

/**
 * Called for each interaction with a ThingServer, e.g., reading or writing of 
 * values, invocation of actions, etc.
 */
public interface InteractionListener {
	/**
	 * Called whenever a property is read.<p>
	 * 
	 * This method is called before the value of the property is read by the
	 * ThingServer. It is therefore possible to modify the value returned 
	 * to the client from this callback, e.g., by calling 
	 * {@link ThingServer#setProperty(String, Object)}. However there is no
	 * guarantee that the client will see exactly this value as there might
	 * be multiple concurrent callback invocations.<p>
	 * 
	 * A typical scenarios where such behavior is acceptable is the on-demand
	 * acquisition of sensor readings. In this case there is no problem if the
	 * client sees a value written from another callback as this is similarly
	 * fresh as the value acquired by this callback.
	 * 
	 * @param thingServer the server affected by the interaction, never null
	 */
	void onReadProperty(String propertyName, ThingServer thingServer);

	void onWriteProperty(String propertyName, Content newValue, ThingServer thingServer);
}
