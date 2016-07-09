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

import java.util.function.Function;

import de.thingweb.desc.ThingDescriptionParser;
import de.thingweb.thing.Thing;

/**
 * This class uses the MockupLauncher and registers functions (e.g., by incrementing values)
 * 
 * <p>Note: Any functions are possible such as random values et cetera</p>
 * 
 * @author https://github.com/danielpeintner
 *
 */

public class MockupLauncherExample {

	static final boolean composed = false;
	
	public static void main(String[] args) throws Exception {
		// load/parse thing description
		final Thing basicLedDesc;
		if(composed) {
			basicLedDesc = ThingDescriptionParser.fromBytes(basic_led_composed.getBytes());
		} else {
			basicLedDesc = ThingDescriptionParser.fromBytes(basic_led_beijing.getBytes());
		}
		// final Thing basicLedDesc = ThingDescriptionParser.fromFile("basic_led.jsonld");
		
		
		MockupLauncher launcher = new MockupLauncher(basicLedDesc);
		
		
		if(composed) {
			// Note: no function registration means reporting values that have been set
		} else {
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
		}
		
		
		

		
		
		launcher.start();
	}
	
	final static String basic_led_beijing = "{\r\n" + 
			"	\"@context\": [\"http://w3c.github.io/wot/w3c-wot-td-context.jsonld\"],\r\n" + 
			"	\"@type\": \"Thing\",\r\n" + 
			"	\"name\": \"basicLed\",\r\n" + 
			"	\"encodings\": [\"JSON\"],\r\n" + 
			"	\"properties\": [{\r\n" + 
			"		\"name\": \"brightness\",\r\n" + 
			"		\"valueType\": {\r\n" + 
			"			\"type\": \"integer\",\r\n" + 
			"			\"minimum\": 0,\r\n" + 
			"			\"maximum\": 255\r\n" + 
			"		},\r\n" + 
			"		\"writable\": true,\r\n" + 
			"		\"hrefs\": [\"brightness\"]\r\n" + 
			"	}, {\r\n" + 
			"		\"name\": \"rgbValueRed\",\r\n" + 
			"		\"valueType\": {\r\n" + 
			"			\"type\": \"integer\",\r\n" + 
			"			\"minimum\": 0,\r\n" + 
			"			\"maximum\": 255\r\n" + 
			"		},\r\n" + 
			"		\"writable\": true,\r\n" + 
			"		\"hrefs\": [\"rgbValueRed\"]\r\n" + 
			"	}, {\r\n" + 
			"		\"name\": \"rgbValueGreen\",\r\n" + 
			"		\"valueType\": {\r\n" + 
			"			\"type\": \"integer\",\r\n" + 
			"			\"minimum\": 0,\r\n" + 
			"			\"maximum\": 255\r\n" + 
			"		},\r\n" + 
			"		\"writable\": true,\r\n" + 
			"		\"hrefs\": [\"rgbValueGreen\"]\r\n" + 
			"	}, {\r\n" + 
			"		\"name\": \"rgbValueBlue\",\r\n" + 
			"		\"valueType\": {\r\n" + 
			"			\"type\": \"integer\",\r\n" + 
			"			\"minimum\": 0,\r\n" + 
			"			\"maximum\": 255\r\n" + 
			"		},\r\n" + 
			"		\"writable\": true,\r\n" + 
			"		\"hrefs\": [\"rgbValueBlue\"]\r\n" + 
			"	}, {\r\n" + 
			"		\"name\": \"snakes\",\r\n" + 
			"		\"valueType\": {\r\n" + 
			"			\"type\": \"integer\"\r\n" + 
			"		},\r\n" + 
			"		\"writable\": false,\r\n" + 
			"		\"hrefs\": [\"snakes\"]\r\n" + 
			"	}],\r\n" + 
			"	\"actions\": [{\r\n" + 
			"		\"name\": \"startSnake\",\r\n" + 
			"		\"hrefs\": [\"startSnake\"]\r\n" + 
			"	}, {\r\n" + 
			"		\"name\": \"stopSnake\",\r\n" + 
			"		\"hrefs\": [\"stopSnake\"]\r\n" + 
			"	}]\r\n" + 
			"}"; 
	
	
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

	
	static final String basic_led_composed = "{\r\n" + 
			"  \"@context\": [\"http://w3c.github.io/wot/w3c-wot-td-context.jsonld\"],\r\n" + 
			"  \"@type\": \"Thing\",\r\n" + 
			"  \"name\": \"MyComposedRGBThing\",\r\n" + 
			"  \"uris\": [\"coap://mytemp.example.com:5683/\"],\r\n" + 
			"  \"encodings\": [\"JSON\"],\r\n" + 
			"  \"properties\": [\r\n" + 
			"    {\r\n" + 
			"		\"name\": \"RGB\",\r\n" + 
			"		\"valueType\": {\r\n" + 
			"			\"title\": \"RGB color\",\r\n" + 
			"			\"type\": \"object\",\r\n" + 
			"			\"properties\": {\r\n" + 
			"				\"red\": {\r\n" + 
			"					\"type\": \"integer\",\r\n" + 
			"					\"minimum\": 0,\r\n" + 
			"					\"maximum\": 255\r\n" + 
			"				},\r\n" + 
			"				\"green\": {\r\n" + 
			"					\"type\": \"integer\",\r\n" + 
			"					\"minimum\": 0,\r\n" + 
			"					\"maximum\": 255\r\n" + 
			"				},\r\n" + 
			"				\"blue\": {\r\n" + 
			"					\"type\": \"integer\",\r\n" + 
			"					\"minimum\": 0,\r\n" + 
			"					\"maximum\": 255\r\n" + 
			"				}\r\n" + 
			"			},\r\n" + 
			"			\"required\": [\"red\", \"green\", \"blue\"]\r\n" + 
			"		},\r\n" + 
			"		\"writable\": true,\r\n" + 
			"		\"hrefs\": [\"rgb\"]\r\n" + 
			"    }\r\n" + 
			"  ]\r\n" + 
			"}";
	
}
