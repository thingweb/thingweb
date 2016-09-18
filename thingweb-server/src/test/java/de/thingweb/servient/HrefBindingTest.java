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

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.thingweb.security.TokenRequirements;
import de.thingweb.thing.Property;
import de.thingweb.thing.Thing;

public class HrefBindingTest {

	@Before
	public void setUp() throws Exception {
		// init servient
		ServientBuilder.initialize();
	}

	@Test
	public void testMultipleHrefs() throws Exception {
		// create GoPiGo thing
		Thing goPiGo = new Thing("GoPiGoTest");
		assertTrue(goPiGo != null);
		
		ObjectNode valueTypeSpeed = JsonNodeFactory.instance.objectNode().put("type", "integer").put("minimum", 0)
				.put("maximum", 255);

		Property pSpeedLeft = new Property.Builder("speedLeft").setValueType(valueTypeSpeed)
				.setHrefs(Arrays.asList(new String[] { "speedLeft", "sl" })).setWriteable(true).build();
		assertTrue(pSpeedLeft != null);
		goPiGo.addProperty(pSpeedLeft);

		final TokenRequirements tokenRequirements = null;
		ThingServer server = ServientBuilder.newThingServer(tokenRequirements);


		ThingInterface ti = server.addThing(goPiGo);
		assertTrue(ti != null);
	}
	
	@Test
	public void testSingleHref() throws Exception {
		// create GoPiGo thing
		Thing goPiGo = new Thing("GoPiGo");
		assertTrue(goPiGo != null);
		
		ObjectNode valueTypeSpeed = JsonNodeFactory.instance.objectNode().put("type", "integer").put("minimum", 0)
				.put("maximum", 255);

		List<String> al = new ArrayList<>();
		al.add("speedLeft");
		Property pSpeedLeft = new Property.Builder("speedLeft").setValueType(valueTypeSpeed)
				.setHrefs(al).setWriteable(true).build();
		assertTrue(pSpeedLeft != null);
		goPiGo.addProperty(pSpeedLeft);

		final TokenRequirements tokenRequirements = null;
		ThingServer server = ServientBuilder.newThingServer(tokenRequirements);


		ThingInterface ti = server.addThing(goPiGo);
		assertTrue(ti != null);
	}

	@After
	public void tearDown() throws IOException {
		ServientBuilder.stop();
	}

}
