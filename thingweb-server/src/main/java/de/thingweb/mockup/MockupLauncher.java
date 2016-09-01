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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

		Thing mockup = new Thing("Room_Automation_CNB_H110");

		List<String> hrefs = new ArrayList<String>();
		hrefs.add("HVAC/RoomTemperature");
//		hrefs.add("HVAC/RT");

		mockup.addProperty(new Property.Builder("Room_temperature")

				.setValueType("BACtype:Real")

				.setPropertyType("[\"HVAC:TemperatureSensor\", \"BACnet:AnalogInputObject:presentValue\" ]")

				.setHrefs(hrefs).build());

		MockupLauncher launcher = new MockupLauncher(mockup);
		
		ThingInterface thingIfc = launcher.start();

		thingIfc.setProperty("Room_temperature", 22.3);

	}

//	static class NicePlugFestTokenReqFactory {
//
//		private static final String ISSUER = "NicePlugfestAS";
//		private static final String AUDIENCE = "NicePlugfestRS";
//		private static final String PUBKEY_ES256 = "{\"keys\":[{\"kty\": \"EC\",\"d\": \"_hysUUk5sRGAHhl7RJN7x5UhBMiy6pl6kHR5-ZaWzpU\",\"use\": \"sig\",\"crv\": \"P-256\",\"kid\": \"PlugFestNice\",\"x\": \"CQsJZUvJWx5yB5EwuipDXRDye4Ybg0wwqxpGgZtcl3w\",\"y\": \"qzYskD2N7GrGDSgo6N9pPLXMIwr6jowFGyqsTJGmpz4\",\"alg\": \"ES256\"},{\"kty\": \"oct\",\"kid\": \"018c0ae5-4d9b-471b-bfd6-eef314bc7037\",\"use\": \"sig\",\"alg\": \"HS256\",\"k\": \"aEp0WElaMnVTTjVrYlFmYnRUTldicGRtaGtWOEZKRy1PbmJjNm14Q2NZZw==\"}]}";
//		// private static final String SUBJECT =
//		// "0c5f83a7-cf08-4f48-8337-bfc65ea149ff";
//		private static final String TYPE = "org:w3:wot:jwt:as:min";
//
//		public static TokenRequirements createTokenRequirements() {
//			return TokenRequirements.build().setIssuer(ISSUER).setAudience(AUDIENCE).setVerificationKeys(PUBKEY_ES256)
//					.setTokenType(TYPE).createTokenRequirements();
//		}
//	}

}
