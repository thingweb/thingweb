/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Siemens AG and the thingweb community
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package de.thingweb.mockup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import de.thingweb.security.TokenRequirements;
import de.thingweb.servient.ServientBuilder;
import de.thingweb.servient.ThingInterface;
import de.thingweb.servient.ThingServer;
import de.thingweb.thing.Property;
import de.thingweb.thing.Thing;

/**
 * Demo class allowing to fake an actual device by setting and reading
 * properties
 * 
 * <p>
 * Note: By registering functions one can define how to report values
 * </p>
 * 
 * @author https://github.com/danielpeintner
 *
 */
public class MockupLauncher {

	private static final Logger log = LoggerFactory.getLogger(MockupLauncher.class);

	ThingInterface ti;
	
	Map<String, Function<Object, Object>> propertyFunctions = new HashMap<>();

	// TODO should we allow multiple things?
	public MockupLauncher(final Thing thingDesc) throws Exception {
		ServientBuilder.initialize();
		final TokenRequirements tokenRequirements = null; // NicePlugFestTokenReqFactory.createTokenRequirements();
		ThingServer server = ServientBuilder.newThingServer(tokenRequirements);

		ti = server.addThing(thingDesc);
	}

	public ThingInterface start() throws Exception {
		attachHandlers(ti);

		ServientBuilder.start();
		
		return ti;
	}


	public void registerOnPropertyUpdate(String propertyName, Function<Object, Object> func) {
		propertyFunctions.put(propertyName, func);
	}

	void attachHandlers(final ThingInterface ti) {
		Map<String, Object> thingProps = new HashMap<String, Object>();

		List<Property> properties = ti.getThingModel().getProperties();

		for (Property property : properties) {
			// Initialize property value
			property.getValueType();
			// Object initValue = new Integer(0);
			// ti.setProperty(property, initValue);

			// register onPropertyUpdate
			ti.onPropertyUpdate(property.getName(), (input) -> {
				log.info("setting " + property.getName() + " value to " + input);
				 thingProps.put(property.getName(), input);
			});
		}

		Consumer<Object> callback = (input) -> {
			if (input instanceof Property) {

				Property property = (de.thingweb.thing.Property) input;

				Function<Object, Object> func = propertyFunctions.get(property.getName());

				// Note: if func is null check for "all covering" function
				if (func == null) {
					func = propertyFunctions.get(null);
				}

				if (func != null) {
					Object value = thingProps.get(property.getName());

					Object result = func.apply(value);

					thingProps.put(property.getName(), result);
					ti.setProperty(property, result);
				}
			}
		};

		ti.onPropertyRead(callback);
	}

	public static void main(String[] args) throws Exception {
		
		// Note: One can also set specific ports
		// e.g., ServientBuilder.getCoapBinding().setPort(5699);
		// ServientBuilder.getHttpBinding().setPort(8081);

		Thing mockup = new Thing("Room_Automation");

		mockup.addProperty(new Property.Builder("Room_temperature")
				.setValueType(JsonNodeFactory.instance.textNode("{ \"type\": \"number\" }"))
				.setWriteable(true).build());

		MockupLauncher launcher = new MockupLauncher(mockup);
		
		ThingInterface thingIfc = launcher.start();

		thingIfc.setProperty("Room_temperature", 22.3);

	}

}
