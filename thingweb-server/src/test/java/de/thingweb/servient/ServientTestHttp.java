package de.thingweb.servient;

import de.thingweb.desc.DescriptionParser;
import de.thingweb.desc.pojo.ThingDescription;
import de.thingweb.thing.Content;
import de.thingweb.thing.MediaType;
import de.thingweb.thing.Thing;
import de.thingweb.util.encoding.ContentHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

import static de.thingweb.servient.TestTools.fromUrl;
import static de.thingweb.servient.TestTools.readResource;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by Johannes on 20.12.2015.
 */
public class ServientTestHttp {

    private ThingServer server;
    private Thread serverThread;
    private ThingInterface thing;

    @Before
    public void setUp() throws Exception {
        ServientBuilder.initialize();
        String json = readResource("simplething.jsonld");
        ThingDescription thingDescription = DescriptionParser.fromBytes(json.getBytes());
        Thing simpleThing = new Thing(thingDescription);
        server = ServientBuilder.newThingServer();
        thing = server.addThing(simpleThing);
        ServientBuilder.start();
    }

    @Test
    public void ensureThingIsReturned() {
        ThingInterface simpleThing = server.getThing("SimpleThing");
        assertThat("should be the same instance",simpleThing,is(thing));
    }

    @Test
    public void setAndReadPropertyDirectly() throws Exception {
        thing.setProperty("number",42);
        Integer number = (Integer) server.getThing("SimpleThing").getProperty("number");
        assertThat("should be Integer",number.getClass(),equalTo(Integer.class));
        assertThat("value is 42", number,is(42));
    }


    @Test
    public void setDirectlyAndReadHttp() throws Exception {
        thing.setProperty("number",42);

        String json = fromUrl("http://localhost:8080/things/SimpleThing/number");

        Content resp = new Content(json.getBytes(), MediaType.APPLICATION_JSON);
        Object number = ContentHelper.getValueFromJson(resp);

        assertThat("may not be null",number,is(notNullValue()));
        assertThat("should be Integer",number.getClass(),equalTo(Integer.class));
        assertThat("value is 42", number,is(42));
    }

    @Test
    public void setDirectlyAndReadHttp_lowercese() throws Exception {
        thing.setProperty("number",42);

        String json = fromUrl("http://localhost:8080/things/simplething/number");

        Content resp = new Content(json.getBytes(), MediaType.APPLICATION_JSON);
        Object number = ContentHelper.getValueFromJson(resp);

        assertThat("should be Integer",number.getClass(),equalTo(Integer.class));
        assertThat("value is 42", number,is(42));
    }

    @Test
    public void attachListenerAndsetDirectly() throws Exception {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        thing.onInvoke("testaction", (nv) -> {
            Integer newVal = ContentHelper.ensureClass(nv,Integer.class);
            future.complete(newVal);
            return null;
        });

        TestTools.postHttpJson("http://localhost:8080/things/simplething/testaction","42");

        assertThat("value is 42", future.get() ,is(42));
    }


    @After
    public void tearDown() throws IOException {
        ServientBuilder.stop();
    }


}