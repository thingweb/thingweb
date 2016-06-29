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

package de.thingweb.servient.impl;

import de.thingweb.binding.AbstractRESTListener;
import de.thingweb.thing.Content;
import de.thingweb.thing.MediaType;
import de.thingweb.thing.Property;
import de.thingweb.util.encoding.ContentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Johannes on 07.10.2015.
 */
public class PropertyListener extends AbstractRESTListener implements Observer {
    private static final Logger log = LoggerFactory.getLogger(PropertyListener.class);
    private final Property property;
    private final ServedThing servedThing;
    private final boolean isObservable;

    public PropertyListener(ServedThing servedThing, Property property) {
        this.property = property;
        this.servedThing = servedThing;
        this.isObservable = property.isObservable();
        property.addObserver(this);
    }
    
    public boolean isObservable(){
    	return isObservable;
    }

    public void setClientObservationState(boolean isObserving){
    	property.isClientObserving = isObserving;
    }
    
    @Override
    public Content onGet() throws Exception {
        if (!property.isReadable()) {
            throw new UnsupportedOperationException();
        }

        Object res = servedThing.getProperty(property);
        if(res instanceof Exception)
        	throw (Exception)res;
        boolean containsMediaType = property.getMetadata().contains("encoding");
        if(containsMediaType)
        	return new Content((byte[])res, property.getMetadata().get("encoding"));
        else
        	return ContentHelper.makeStringValue((String)res);
        //return ContentHelper.makeJsonValue(res);

    }

    @Override
    public Content onPut(Content data) throws RuntimeException {
        if (!property.isWritable()) {
            throw new UnsupportedOperationException(property.getName() + " is not writable");
        }
        String strContent = new String(data.getContent());
        String valueJson;
		try {
			valueJson = ContentHelper.getValueStringFromJson(data);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
        Object res = servedThing.updateProperty(property, valueJson);
        if(res instanceof Exception)
        	throw (RuntimeException)res;
        return ContentHelper.makeStringValue("{}");
    }


    @Override
    public void update(Observable o, Object arg) {
        log.info("change detected: " + o + " to " + arg);
        setChanged();
        notifyObservers();
    }
}
