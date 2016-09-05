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

import de.thingweb.desc.ThingDescriptionParser;
import de.thingweb.servient.impl.MultiBindingThingServer;
import de.thingweb.thing.Action;
import de.thingweb.thing.Property;
import de.thingweb.thing.Thing;
import de.thingweb.typesystem.jsonschema.JsonTypeHelper;

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
        Thing thingDescription = ThingDescriptionParser.fromBytes(json.getBytes());
        assertThat(thingDescription.getName(),equalTo("SimpleThing"));
    }

    @Test
    public void createThing() throws IOException, URISyntaxException {
        String json = readResource("simplething.jsonld");
        Thing simpleThing = ThingDescriptionParser.fromBytes(json.getBytes());
        assertThat(simpleThing.getAction("testaction"),notNullValue());
        assertThat(simpleThing.getProperty("number"),notNullValue());
    }

    @Test
    public void createSingleThingServient() throws Exception {
        String json = readResource("simplething.jsonld");
        Thing simpleThing = ThingDescriptionParser.fromBytes(json.getBytes());
        ThingServer server = ServientBuilder.newThingServer(simpleThing);
        ServientBuilder.start();
    }

    @Test
    public void createThingServientAndAddThing() throws Exception {
        String json = readResource("simplething.jsonld");
        Thing simpleThing = ThingDescriptionParser.fromBytes(json.getBytes());
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
        Thing simpleThing = ThingDescriptionParser.fromBytes(json.getBytes());
        ThingServer server = ServientBuilder.newThingServer();
        ServientBuilder.start();
        server.addThing(simpleThing);
    }

    @Test
    public void addPartialThing() throws Exception {
        String json = readResource("interactiononly.jsonld");
        Thing partialThing = ThingDescriptionParser.fromBytes(json.getBytes());
        ThingServer server = ServientBuilder.newThingServer();
        ServientBuilder.start();
        server.addThing(partialThing);
    }
    
    @Test
    // see https://github.com/thingweb/thingweb/issues/21
    public void testJScreationOfTD() throws Exception {
    	ThingServer server  = ServientBuilder.newThingServer();
    	
        Thing srvThing = new Thing("servient");

        srvThing.addProperties(
            Property.getBuilder("numberOfThings").setValueType(JsonTypeHelper.integerType()).setWriteable(false).build(),
            Property.getBuilder("securityEnabled").setValueType(JsonTypeHelper.booleanType()).setWriteable(true).build()
        );

        srvThing.addActions(
            Action.getBuilder("createThing").setInputType(JsonTypeHelper.stringType()).build(),
            Action.getBuilder("addScript").setInputType(JsonTypeHelper.stringType()).build(),
            Action.getBuilder("reset").build()
        );

        ThingInterface serverInterface = server.addThing(srvThing);
        
        Thing tm = serverInterface.getThingModel();
        
        // Properties
        Property p1 = tm.getProperty("numberOfThings");
        assertTrue(p1.getHrefs().contains(MultiBindingThingServer.urlize("numberOfThings")));
        Property p2 = tm.getProperty("securityEnabled");
        assertTrue(p2.getHrefs().contains(MultiBindingThingServer.urlize("securityEnabled")));
        
        // Actions
        Action a1 = tm.getAction("createThing");
        assertTrue(a1.getHrefs().contains(MultiBindingThingServer.urlize("createThing")));
        Action a2 = tm.getAction("addScript");
        assertTrue(a2.getHrefs().contains(MultiBindingThingServer.urlize("addScript")));
        Action a3 = tm.getAction("reset");
        assertTrue(a3.getHrefs().contains(MultiBindingThingServer.urlize("reset")));
        
        
//        System.out.println(tm);
    }
    
    

    @After
    public void tearDown() throws IOException {
        ServientBuilder.stop();
    }

}