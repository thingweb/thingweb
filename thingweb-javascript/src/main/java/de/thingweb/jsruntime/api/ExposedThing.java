package de.thingweb.jsruntime.api;

import de.thingweb.servient.ThingInterface;
import de.thingweb.servient.ThingServer;
import de.thingweb.thing.Action;
import de.thingweb.thing.Property;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Johannes on 09.12.2015.
 */
public class ExposedThing {

    private final ThingInterface thing;
    public final String name;
    private final ThingServer servient;

    protected ExposedThing(ThingInterface thing, ThingServer thingServer) {
        this.thing = thing;
        this.name = thing.getName();
        this.servient = thingServer;
    }

    public static ExposedThing from(ThingInterface servedThing, ThingServer thingServer) {
        return new ExposedThing(servedThing,thingServer);
    }

    public ExposedThing onInvokeAction(String actionName, Function callback) {
        thing.onActionInvoke(actionName,callback);
        return this;
    }

    public ExposedThing onUpdateProperty(String propertyName, Consumer callback) {
        thing.onPropertyUpdate(propertyName,callback);
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

    public ExposedThing addEvent(String eventName, ScriptObjectMirror type) {
        throw new RuntimeException("Events are not yet implemented");
        //return this;
    }

    public ExposedThing addAction(String actionName, String inputType, String outputType) {
        Action action = Action.getBuilder(actionName)
                .setInputType(inputType)
                .setOutputType(outputType)
                .build();

        thing.addAction(action);
        servient.rebind(name);
        return this;
    }

    public ExposedThing addAction(String actionName, String inputType) {
        return addAction(actionName, inputType, null);
    }

    public ExposedThing addAction(String actionName) {
        return addAction(actionName, null, null);
    }

    public ExposedThing addProperty(String propName, String type) {
        Property prop = Property.getBuilder(propName)
                .setXsdType(type)
                .build();

        thing.addProperty(prop);
        servient.rebind(name);
        return this;
    }


}
