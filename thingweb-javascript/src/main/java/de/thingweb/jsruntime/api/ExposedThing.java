package de.thingweb.jsruntime.api;

import de.thingweb.servient.ServientBuilder;
import de.thingweb.servient.ThingInterface;
import de.thingweb.servient.ThingServer;
import de.thingweb.thing.Action;
import de.thingweb.thing.Property;
import de.thingweb.thing.Thing;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Johannes on 09.12.2015.
 */
public class ExposedThing {

    private final ThingInterface thing;

    protected ExposedThing(ThingInterface thing) {
        this.thing = thing;
    }

    public static ExposedThing from(ThingInterface servedThing) {
        return new ExposedThing(servedThing);
    }

    public ExposedThing onInvoke(String actionName, Function callback) {
        thing.onInvoke(actionName,callback);
        return this;
    }


    public ExposedThing onUpdate(String propertyName, Consumer callback) {
        thing.onUpdate(propertyName,callback);
        return this;
    }

    public ExposedThing setProperty(String propertyName, Object value) {
        thing.setProperty(propertyName,value);
        return this;
    }

    public Object getProperty(String propertyName) {
        return thing.getProperty(propertyName);
    }

    public Object invokeAction(String actionName, Object parameter) {
        return thing.invokeAction(actionName,parameter);
    }

}
