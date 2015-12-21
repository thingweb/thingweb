package de.thingweb.jsruntime.api;

import de.thingweb.client.Callback;
import de.thingweb.client.Client;
import de.thingweb.client.UnsupportedException;
import de.thingweb.client.lazy.AbstractCallback;
import de.thingweb.jsruntime.JsPromise;
import de.thingweb.thing.Content;
import de.thingweb.thing.Thing;
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
            public void onPutError(String propertyName) {
                promise.reject("error setting property " + propertyName);
            }
        };

        try {
            client.put(propertyName,ContentHelper.makeJsonValue(property),myCb);
        } catch (UnsupportedException e) {
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
                promise.resolve(response.getContent());
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

    public JsPromise callAction(String actionName, Object param) {
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
