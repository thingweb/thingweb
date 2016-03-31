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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import de.thingweb.thing.Action;
import de.thingweb.thing.Property;
import de.thingweb.thing.Thing;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by Johannes on 03.01.2016.
 */
public class MultiThingTests {

    private ObjectMapper jsonMapper;
    private ThingServer server;

    @Before
    public void setUp() throws Exception {
        ServientBuilder.initialize();
        jsonMapper = new ObjectMapper();
        server = ServientBuilder.newThingServer();
    }

	@Test
    public void multiThingServient() throws Exception {
        int nthings =10;
        Thing[] things = new Thing[nthings];

        Action testAction = Action.getBuilder("testAction").build();
        Property testProp = new Property(Property.getBuilder("testProp").setWriteable(true).setXsdType("xsd:string").build());

        for(int i = 0; i<nthings; i++) {
            things[i] = new Thing("thing" + i);
            things[i].addAction(testAction);
            things[i].addProperty(testProp);
            server.addThing(things[i]);
        }

        ServientBuilder.start();

        JsonNode node = jsonMapper.readTree(new URL("http://localhost:8080/things/"));
        assertThat("expecting an array of links to things",node.isArray(), is(true));

        ArrayNode links = (ArrayNode) node;

        assertThat("expected at least the same number of links under /things as things", links.size(), greaterThanOrEqualTo(nthings));

        links.forEach(link -> {
            assertThat(link.get("href").textValue(),startsWith("/things/"));
            assertThat(link.get("href").textValue(),not(isEmptyOrNullString()));
        });
    }

    @Test
    public void notUrlConformNames() throws Exception {
        final Thing thing = new Thing("Ugly strange näime");
        thing.addProperty(new Property(new PropertyDescription("not url kompätibel")));

        thing.addAction(Action.getBuilder("wierdly named äktschn").build());

        server.addThing(thing);

        ServientBuilder.start();

        URL thingroot = new URL("http://localhost:8080/things/");

        final ArrayNode jsonNode = (ArrayNode) jsonMapper.readTree(thingroot);
        final String thingHref = jsonNode.get(0).get("href").textValue();

        // this replacement needs to be done when writing the json
        final URL thingUrl = new URL(thingroot,thingHref);
        final ArrayNode thingLinks = (ArrayNode) jsonMapper.readTree(thingUrl);

        thingLinks.forEach(node -> {
            try {
                jsonMapper.readTree(new URL(thingUrl,node.get("href").textValue()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void main(String[] args) throws Exception {
        final Thing thing = new Thing("Ugly strange näime");
        thing.addProperty(new Property(new PropertyDescription("not url kompätibel")));
        thing.addAction(Action.getBuilder("wierdly named äktschn").build());

        ServientBuilder.initialize();
        ServientBuilder.newThingServer(thing);
        ServientBuilder.start();

    }

    @After
    public void tearDown() throws IOException {
        ServientBuilder.stop();
    }

}
