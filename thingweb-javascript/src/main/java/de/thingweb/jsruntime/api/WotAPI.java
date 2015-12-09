package de.thingweb.jsruntime.api;

import de.thingweb.client.ClientFactory;
import de.thingweb.client.UnsupportedException;
import de.thingweb.desc.DescriptionParser;
import de.thingweb.desc.pojo.ThingDescription;
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

//    public void callJava() {
//        System.out.println("Java function called from js");
//    }
//
//    public void toJava(ScriptObjectMirror obj) {
//        System.out.println("Java got an object from js: " + obj.getClassName());
//    }
//
//    public void callMe(Function fun) {
//        System.out.println("Java got a callback from js: " + fun.getClass());
//        fun.apply("hello!");
//    }

    private static ClientFactory cf;
    private static ClientFactory getClientFactory() {
        if(cf == null)
            cf = new ClientFactory();
        return cf;
    }

    public ExposedThing exposeFromUri(String uri) {
        try {
            ThingDescription description = DescriptionParser.fromURL(new URL(uri));
            return ExposedThing.from(description);
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
            ThingDescription description = DescriptionParser.fromBytes(jsonld.getBytes());
            return ExposedThing.from(description);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ConsumedThing consume(String jsonld) {
        try {
            ThingDescription description = DescriptionParser.fromBytes(jsonld.getBytes());
            return ConsumedThing.from(getClientFactory().getClientFromTD(description));
        } catch (IOException | UnsupportedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
