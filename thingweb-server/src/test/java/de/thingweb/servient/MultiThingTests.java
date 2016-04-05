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
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.thingweb.desc.ThingDescriptionParser;
import de.thingweb.thing.Action;
import de.thingweb.thing.Property;
import de.thingweb.thing.Thing;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;

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
        Property testProp = Property.getBuilder("testProp").setWriteable(true).setXsdType("xsd:string").build();

        for(int i = 0; i<nthings; i++) {
            things[i] = new Thing("thing" + i);
            things[i].addAction(testAction);
            things[i].addProperty(testProp);
            server.addThing(things[i]);
        }

        ServientBuilder.start();

        JsonNode jsonNode = jsonMapper.readTree(new URL("http://localhost:8080/things/"));
        assertThat("should be an object", jsonNode.isObject(), is(true));

        final ObjectNode repo = (ObjectNode) jsonNode;

        assertThat("expected at least the same number of links under /things as things", repo.size(), greaterThanOrEqualTo(nthings));

        Arrays.stream(things).forEach(
                thing -> assertThat("should contain all things, misses " + thing.getName(),repo.get(thing.getName()),notNullValue())
        );

        //further checks
    }

    @Test
    public void notUrlConformNames() throws Exception {
        final String thingName = ensureUTF8("Ugly strange näime");
        final Thing thing = new Thing(thingName);

        final String propertyName = ensureUTF8("not url kompätibel");
        thing.addProperty(Property.getBuilder(propertyName).build());

        final String actionName = ensureUTF8("wierdly named äktschn");
        thing.addAction(Action.getBuilder(actionName).build());

        server.addThing(thing);

        ServientBuilder.start();
        URL thingroot = new URL("http://localhost:8080/things/");

        final JsonNode jsonNode = jsonMapper.readTree(thingroot);
        assertThat("should be an object", jsonNode.isObject(), is(true));

        final ObjectNode repo = (ObjectNode) jsonNode;
        final JsonNode thingDesc = repo.get(thingName);
        assertThat(thingDesc,notNullValue());
        assertThat(thingDesc.isObject(), is(true));

        // check if thingdesc is parseable
        final Thing thing1 = ThingDescriptionParser.fromJavaMap(thingDesc);

        assertThat("should be the same name",thing1.getName(), equalTo(thing.getName()));
        assertThat("should contain an action", thing1.getActions(), hasSize(greaterThanOrEqualTo(1)));
        assertThat("property name should be the same", thing1.getActions().get(0).getName(), equalTo(actionName));
        assertThat("should contain a property", thing1.getProperties(), hasSize(greaterThanOrEqualTo(1)));
        assertThat("action name should be the same",thing1.getProperties().get(0).getName(), equalTo(propertyName));
    }

    /** this is a crutch since I cannot get Gradle to compile using UTF-8.
     * I am not sure why anybody would not want UTF-8 as the default setting...
     * @param input string literal from the file
     * @return string in utf-8
     */
    public static String ensureUTF8(String input) {
        return String.valueOf(Charset.forName("UTF-8").encode(input));
    }

    @After
    public void tearDown() throws IOException {
        ServientBuilder.stop();
    }

}
