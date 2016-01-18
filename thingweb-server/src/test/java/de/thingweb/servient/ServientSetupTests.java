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

import de.thingweb.desc.DescriptionParser;
import de.thingweb.desc.pojo.ThingDescription;
import de.thingweb.thing.Thing;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static de.thingweb.servient.TestTools.readResource;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Created by Johannes on 20.12.2015.
 */
public class ServientSetupTests {

    @Before
    public void setUp() throws Exception {
        ServientBuilder.initialize();
    }

    @Test
    public void readJson() throws IOException, URISyntaxException {
        String json = readResource("simplething.jsonld");
        System.out.println(json);
    }

    @Test
    public void parseTDFromJson() throws IOException, URISyntaxException {
        String json = readResource("simplething.jsonld");
        ThingDescription thingDescription = DescriptionParser.fromBytes(json.getBytes());
        assertThat(thingDescription.getMetadata().getName(),equalTo("SimpleThing"));
    }

    @Test
    public void createThing() throws IOException, URISyntaxException {
        String json = readResource("simplething.jsonld");
        ThingDescription thingDescription = DescriptionParser.fromBytes(json.getBytes());
        Thing simpleThing = new Thing(thingDescription);
        assertThat(simpleThing.getAction("testaction"),notNullValue());
        assertThat(simpleThing.getProperty("number"),notNullValue());
    }

    @Test
    public void createSingleThingServient() throws Exception {
        String json = readResource("simplething.jsonld");
        ThingDescription thingDescription = DescriptionParser.fromBytes(json.getBytes());
        Thing simpleThing = new Thing(thingDescription);
        ThingServer server = ServientBuilder.newThingServer(simpleThing);
        ServientBuilder.start();
    }

    @Test
    public void createThingServientAndAddThing() throws Exception {
        String json = readResource("simplething.jsonld");
        ThingDescription thingDescription = DescriptionParser.fromBytes(json.getBytes());
        Thing simpleThing = new Thing(thingDescription);
        ThingServer server = ServientBuilder.newThingServer();
        server.addThing(simpleThing);
        ServientBuilder.start();
    }

    @Test
    public void startEmptyServient() throws Exception {
        ThingServer server = ServientBuilder.newThingServer();
        ServientBuilder.start();
    }

    @Test
    public void addAfterStart() throws Exception {
        String json = readResource("simplething.jsonld");
        ThingDescription thingDescription = DescriptionParser.fromBytes(json.getBytes());
        Thing simpleThing = new Thing(thingDescription);
        ThingServer server = ServientBuilder.newThingServer();
        ServientBuilder.start();
        server.addThing(simpleThing);
    }

    @Test
    public void addPartialThing() throws Exception {
        String json = readResource("interactiononly.jsonld");
        ThingDescription thingDescription = DescriptionParser.fromBytes(json.getBytes());
        Thing partialThing = new Thing(thingDescription);
        ThingServer server = ServientBuilder.newThingServer();
        ServientBuilder.start();
        server.addThing(partialThing);
    }

    @After
    public void tearDown() throws IOException {
        ServientBuilder.stop();
    }

}