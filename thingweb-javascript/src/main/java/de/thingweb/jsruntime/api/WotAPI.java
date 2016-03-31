package de.thingweb.jsruntime.api;

import de.thingweb.client.ClientFactory;
import de.thingweb.client.UnsupportedException;
import de.thingweb.desc.ThingDescriptionParser;
import de.thingweb.servient.ServientBuilder;
import de.thingweb.servient.ThingInterface;
import de.thingweb.servient.ThingServer;
import de.thingweb.thing.Thing;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.function.Function;

/**
 * Created by Johannes on 09.12.2015.
 */
public class WotAPI {
    public static final String version = "0.0.1";

    public String getVersion() {
        return version;
    }

    private static ClientFactory cf;
    private static ClientFactory getClientFactory() {
        if(cf == null)
            cf = new ClientFactory();
        return cf;
    }

    private ThingServer thingServer;
    private ThingServer getThingServer() {
        if(thingServer == null) {
            try {
                thingServer = ServientBuilder.newThingServer();
                ServientBuilder.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return thingServer;
    }

    public WotAPI() {

    }

    public WotAPI(ThingServer thingServer) {
        this.thingServer = thingServer;
    }

    public ExposedThing exposeFromUri(String uri) {
        try {
            Thing thing = ThingDescriptionParser.fromURL(new URL(uri));
            ThingInterface servedThing = getThingServer().addThing(thing);
            return ExposedThing.from(servedThing);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ConsumedThing consumeFromUri(String uri) {
        try {
            return ConsumedThing.from(getClientFactory().getClientUrl(new URI(uri)));
        } catch (IOException | UnsupportedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public ExposedThing expose(String jsonld)  {
        try {
            Thing thing = ThingDescriptionParser.fromBytes(jsonld.getBytes());
            ThingInterface servedThing = getThingServer().addThing(thing);
            return ExposedThing.from(servedThing);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ConsumedThing consume(String jsonld) {
        try {
            Thing description = ThingDescriptionParser.fromBytes(jsonld.getBytes());
            return ConsumedThing.from(getClientFactory().getClientFromTD(description));
        } catch (IOException | UnsupportedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public ExposedThing getLocalThing(String name) {
        return ExposedThing.from(getThingServer().getThing(name));
    }
}
