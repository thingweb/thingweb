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
import de.webthing.thing.Property;


/**
 * The ThingInterface provides methods for interacting with things.
 */
public interface ThingInterface {
	/**
	 * Sets a property to a new value.<p>
	 * 
	 * This method will throw {@link IllegalArgumentException}s if the property
	 * is invalid (e.g. does not belong to the Thing served by this server) or
	 * the value is incompatible with the specified property.
	 * 
	 * @param property the property, must not be null
	 * @param value the new value must not be null
	 */
	void setProperty(Property property, Object value);
	
	
	/**
	 * Sets a property by name.<p>
	 * 
	 * This method will throw an {@link IllegalArgumentException} if no 
	 * property with the given name exists.<p>
	 * 
	 * This method will throw an {@link IllegalArgumentException} if the 
	 * property is invalid (e.g. does not belong to the Thing served by this 
	 * server) or the value is incompatible with the specified property.
	 * 
	 * @param propertyName name of the property to set, must not be null
	 * @param value the new value must not be null
	 */
	void setProperty(String propertyName, Object value);
	
	
	Object getProperty(Property property);
	
	
	Object getProperty(String propertyName);

}
