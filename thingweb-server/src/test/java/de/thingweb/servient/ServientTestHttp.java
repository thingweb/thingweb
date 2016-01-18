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

package de.thingweb.servient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.net.InetAddress;
import java.util.concurrent.CompletableFuture;

import static de.thingweb.servient.TestTools.fromUrl;
import static de.thingweb.servient.TestTools.readResource;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by Johannes on 20.12.2015.
 */
public class ServientTestHttp {

    private ThingServer server;
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

    @Test
    public void readTD() throws Exception {
        String fromSrv = TestTools.fromUrl("http://localhost:8080/things/SimpleThing/.td");
        String orig = readResource("simplething.jsonld");

        orig = orig.replace("localhost", InetAddress.getLocalHost().getHostName());

        ObjectNode srvJson = (ObjectNode) ContentHelper.readJSON(fromSrv);
        JsonNode origJson = ContentHelper.readJSON(orig);

        ThingDescription td = DescriptionParser.fromBytes(fromSrv.getBytes());
        ThingDescription reference = DescriptionParser.fromBytes(orig.getBytes());

        assertThat("jsonld context should match",srvJson.get("@context"),equalTo(origJson.get("@context")));
        assertThat("metadata content should match",srvJson.get("metadata"),equalTo(origJson.get("metadata")));
        assertThat("interactions should match",srvJson.get("interactions"),equalTo(origJson.get("interactions")));

        assertThat(ContentHelper.getJsonMapper().valueToTree(td),equalTo(ContentHelper.getJsonMapper().valueToTree(reference)));
    }

    @After
    public void tearDown() throws IOException {
        ServientBuilder.stop();
    }


}