package de.thingweb.jsruntime.api;

import de.thingweb.client.Client;
import de.thingweb.jsruntime.JsPromise;
import de.thingweb.thing.Thing;

/**
 * Created by Johannes on 09.12.2015.
 */
public class ConsumedThing {

    private final Client client;

    public ConsumedThing(Client client) {
        this.client = client;
    }

    public JsPromise setProperty(String propertyName, Object property) {
        JsPromise promise = new JsPromise();

        //do async:
        //set property on thing
        //then
        //promise.resolve(null_or_new_value);
        //if things go south
        //promise.reject(error);

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

        return promise;
    }

    public static ConsumedThing from(Client client) {
        return new ConsumedThing(client);
    }
}
