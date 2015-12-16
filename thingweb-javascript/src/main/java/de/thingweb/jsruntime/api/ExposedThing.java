package de.thingweb.jsruntime.api;

import de.thingweb.desc.pojo.ThingDescription;
import de.thingweb.servient.ServientBuilder;
import de.thingweb.servient.ThingServer;
import de.thingweb.thing.Thing;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Johannes on 09.12.2015.
 */
public class ExposedThing {


    private final ThingServer thingServer;

    private ExposedThing(Thing thing) {
        this.thingServer = ServientBuilder.newThingServer(thing);
    }

    public static ExposedThing from(ThingDescription description) {
        return new ExposedThing(new Thing(description));
    }

    public void onInvoke(String actionName, Function callback) {
        thingServer.onInvoke(actionName,callback);
    }

    public void onUpdate(String propertyName, Consumer callback) {
        thingServer.onUpdate(propertyName,callback);
    }


}
