/*
 *
 *  * The MIT License (MIT)
 *  *
 *  * Copyright (c) 2016 Siemens AG and the thingweb community
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in
 *  * all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  * THE SOFTWARE.
 *
 */

package de.thingweb.jsruntime.api;

import de.thingweb.client.Callback;
import de.thingweb.client.Client;
import de.thingweb.client.UnsupportedException;
import de.thingweb.client.lazy.AbstractCallback;
import de.thingweb.jsruntime.JsPromise;
import de.thingweb.thing.Content;
import de.thingweb.util.encoding.ContentHelper;

/**
 * Created by Johannes on 09.12.2015.
 */
public class ConsumedThing {

    private final Client client;

    public ConsumedThing(Client client) {
        this.client = client;
    }

    public JsPromise setProperty(String propertyName, Object property) {

        //do async:
        //set property on thing
        //then
        //promise.resolve(null_or_new_value);
        //if things go south
        //promise.reject(error);

        JsPromise promise = new JsPromise();

        Callback myCb = new AbstractCallback() {
            @Override
            public void onPut(String propertyName, Content response) {
                promise.resolve(response.getContent());
            }

            @Override
            public void onPutError(String propertyName, String message) {
                promise.reject("error setting property " + propertyName + ": " + message);
            }
        };

        try {
            client.put(propertyName,ContentHelper.makeJsonValue(property),myCb);
        } catch (Exception e) {
            promise.reject(e);
        }

        return promise;
    }

    public JsPromise getProperty(String propertyName) {
        JsPromise promise = new JsPromise();

        //do async:
        //get property from thing
        //then
        //promise.resolve(value_of_property);
        //if things go south
        //promise.reject(error);

        Callback myCb = new AbstractCallback() {
            @Override
            public void onGet(String propertyName, Content response) {
                promise.resolve(ContentHelper.getValueFromJson(response));
            }

            @Override
            public void onGetError(String propertyName) {
                promise.reject("error getting property " + propertyName);
            }
        };

        try {
            client.get(propertyName,myCb);
        } catch (UnsupportedException e) {
            promise.reject(e);
        }

        return promise;
    }

    public JsPromise invokeAction(String actionName, Object param) {
        JsPromise promise = new JsPromise();

        //do async:
        //call action on thing
        //then
        //promise.resolve(null_or_new_value);
        //if things go south
        //promise.reject(error);

        Callback myCb = new AbstractCallback() {
            @Override
            public void onAction(String actionName, Content response) {
                super.onAction(actionName, response);
            }

            @Override
            public void onActionError(String actionName) {
                promise.reject("error while calling action " + actionName);
            }
        };

        return promise;
    }

    public static ConsumedThing from(Client client) {
        return new ConsumedThing(client);
    }
}
