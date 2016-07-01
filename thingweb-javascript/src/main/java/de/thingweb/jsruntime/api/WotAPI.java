package de.thingweb.jsruntime.api;

import de.thingweb.client.ClientFactory;
import de.thingweb.client.UnsupportedException;
import de.thingweb.desc.ThingDescriptionParser;
import de.thingweb.jsruntime.JsPromise;
import de.thingweb.servient.ServientBuilder;
import de.thingweb.servient.ThingInterface;
import de.thingweb.servient.ThingServer;
import de.thingweb.thing.Thing;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//import jdk.nashorn.api.scripting.ScriptObjectMirror;

/**
 * Created by Johannes on 09.12.2015.
 */
public class WotAPI {
    public static final String version = "0.0.2";
    private static final ExecutorService executor = Executors.newCachedThreadPool();

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

    public JsPromise discover(String method, ScriptObjectMirror filter) {
        JsPromise promise = new JsPromise();

        executor.submit(() -> {
                if(method.equals("registry")) {
                    /*
                        try {
                            promise.resolve(
                                //ConsumedThing.from();
                            );
                        } catch (IOException | UnsupportedException | URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                    */
                } else if(method.equals("local")) {
                    if(filter.containsKey("name")) {
                        final String name = (String) filter.getMember("name");
                        final ExposedThing localThing = getLocalThing(name);
                        final ExposedThing[] things = {localThing};
                        promise.resolve(things);
                    } else {
                        promise.reject(new RuntimeException("No name given for local discovery"));
                    }
                }
        });

        return promise;
    }

    public JsPromise consumeDescriptionUri(String uri) {
        JsPromise promise = new JsPromise();

        executor.submit(() -> {
            try {
                promise.resolve(
                        ConsumedThing.from(
                                getClientFactory().getClientUrl(new URI(uri))
                        )
                );
            } catch (IOException | UnsupportedException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });

        return promise;
    }

    public JsPromise consumeDescription(String jsonld) {
        JsPromise promise = new JsPromise();

        executor.submit(() -> {
            try {
                Thing description = ThingDescriptionParser.fromBytes(jsonld.getBytes());
                promise.resolve(
                        ConsumedThing.from(
                                getClientFactory().getClientFromTD(description)
                        )
                );
            } catch (IOException | UnsupportedException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });

        return promise;
    }

    public JsPromise createFromDescription(String jsonld)  {
        JsPromise promise = new JsPromise();

        executor.submit(() -> {
            try {
                final Thing thing = ThingDescriptionParser.fromBytes(jsonld.getBytes());
                final ThingInterface servedThing = getThingServer().addThing(thing);
                final ExposedThing exposedThing = ExposedThing.from(servedThing, getThingServer());
                promise.resolve(exposedThing);
            } catch (IOException e) {
                promise.reject(new RuntimeException(e));
            }
        });

        return promise;
    }

    public JsPromise createFromDescriptionUri(String uri)  {
        JsPromise promise = new JsPromise();

        executor.submit(() -> {
            try {
                final Thing thing = ThingDescriptionParser.fromURL(new URL(uri));
                final ThingInterface servedThing = getThingServer().addThing(thing);
                final ExposedThing exposedThing = ExposedThing.from(servedThing, getThingServer());
                promise.resolve(exposedThing);
            } catch (IOException e) {
                promise.reject(new RuntimeException(e));
            }
        });

        return promise;
    }


    public JsPromise newThing(String name) {
        JsPromise promise = new JsPromise();

        executor.submit(() -> {
            final Thing thing = new Thing(name);
            final ThingInterface servedThing = getThingServer().addThing(thing);
            final ExposedThing exposedThing = ExposedThing.from(servedThing, getThingServer());
            promise.resolve(exposedThing);
        });

        return promise;
    }


    public ExposedThing getLocalThing(String name) {
        final ThingInterface thing = getThingServer().getThing(name);
        return ExposedThing.from(thing,getThingServer());
    }
}
