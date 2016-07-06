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

package de.thingweb.launcher.demo;

import java.util.function.Function;

import de.thingweb.desc.ThingDescriptionParser;
import de.thingweb.thing.Thing;

/**
 * This class uses the FakeDemoLauncher and registers functions (e.g., by incrementing values)
 * 
 * <p>Note: Any functions are possible such as random values et cetera</p>
 * 
 * @author https://github.com/danielpeintner
 *
 */

public class FakeDemoLauncherExample {

	
	public static void main(String[] args) throws Exception {
		// load/parse thing description
		final Thing basicLedDesc = ThingDescriptionParser.fromBytes(basic_led.getBytes());
		// final Thing basicLedDesc = ThingDescriptionParser.fromFile("basic_led.jsonld");
		
		
		FakeDemoLauncher launcher = new FakeDemoLauncher(basicLedDesc);
		
		// Note: no function registration means reporting value that have been set
		
		// increment existing input value by 1
		Function<Object, Object> funcBrightness = (input) -> {
			if(input instanceof Number) {
				Number n = (Number) input;
				return n.intValue() + 1;
			} else {
				return 0;
			}
		};
		
		// increment existing input value by 2
		Function<Object, Object> funcRest = (input) -> {
			if(input instanceof Number) {
				Number n = (Number) input;
				return n.intValue() + 2;
			} else {
				return 0;
			}
		};
		
		launcher.registerOnPropertyUpdate("brightness", funcBrightness);
		launcher.registerOnPropertyUpdate(null, funcRest); // for all other properties
		
		
		launcher.start();
	}
	
	final static String basic_led = "{\r\n" + 
			"  \"@context\": \"http://w3c.github.io/wot/w3c-wot-td-context.jsonld\",\r\n" + 
			"  \"metadata\": {\r\n" + 
			"    \"name\": \"basicLed\"\r\n" + 
			"  },\r\n" + 
			"  \"interactions\": [\r\n" + 
			"    {\r\n" + 
			"      \"@type\": \"Property\",\r\n" + 
			"      \"name\": \"brightness\",\r\n" + 
			"      \"outputData\": \"xsd:unsignedByte\",\r\n" + 
			"      \"writable\": true\r\n" + 
			"    },\r\n" + 
			"    {\r\n" + 
			"      \"@type\": \"Property\",\r\n" + 
			"      \"name\": \"rgbValueRed\",\r\n" + 
			"      \"outputData\": \"xsd:unsignedByte\",\r\n" + 
			"      \"writable\": true\r\n" + 
			"    },\r\n" + 
			"    {\r\n" + 
			"      \"@type\": \"Property\",\r\n" + 
			"      \"name\": \"rgbValueGreen\",\r\n" + 
			"      \"outputData\": \"xsd:unsignedByte\",\r\n" + 
			"      \"writable\": true\r\n" + 
			"    },\r\n" + 
			"    {\r\n" + 
			"      \"@type\": \"Property\",\r\n" + 
			"      \"name\": \"rgbValueBlue\",\r\n" + 
			"      \"outputData\": \"xsd:unsignedByte\",\r\n" + 
			"      \"writable\": true\r\n" + 
			"    },\r\n" + 
			"    {\r\n" + 
			"      \"@type\": \"Property\",\r\n" + 
			"      \"name\": \"snakes\",\r\n" + 
			"      \"outputData\": \"xsd:int\"\r\n" + 
			"    },\r\n" + 
			"    {\r\n" + 
			"      \"@type\": \"Action\",\r\n" + 
			"      \"name\": \"startSnake\"\r\n" + 
			"    },\r\n" + 
			"    {\r\n" + 
			"      \"@type\": \"Action\",\r\n" + 
			"      \"name\": \"stopSnake\"\r\n" + 
			"    }\r\n" + 
			"  ]\r\n" + 
			"}\r\n" + 
			"";

	
}
