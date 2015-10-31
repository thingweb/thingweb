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

package de.thingweb.launcher;

import de.thingweb.desc.DescriptionParser;
import de.thingweb.leddemo.DemoLedAdapter;
import de.thingweb.servient.ServientBuilder;
import de.thingweb.servient.ThingServer;
import de.thingweb.thing.Content;
import de.thingweb.thing.MediaType;
import de.thingweb.thing.Thing;
import de.thingweb.util.encoding.ContentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


/**
 * Launches a WoT thing.
 */
public class Launcher {

	private static final Logger log = LoggerFactory.getLogger(Launcher.class);
	private static final int STEPLENGTH = 100;

	public static void main(String[] args) throws Exception {
		ServientBuilder.initialize();

		String ledTD = "jsonld" + File.separator + "fancy_led.jsonld";

		Thing led = new Thing(DescriptionParser.fromFile(ledTD));
		ThingServer server = ServientBuilder.newThingServer(led);

		attachHandlers(server);

		ServientBuilder.start();
		}

	public static void attachHandlers(final ThingServer server) {
		DemoLedAdapter realLed = new DemoLedAdapter();

		//init block
		server.setProperty("rgbValueRed",realLed.getRed() & 0xFF);
		server.setProperty("rgbValueGreen",realLed.getGreen() & 0xFF);
		server.setProperty("rgbValueBlue",realLed.getBlue() & 0xFF);
		server.setProperty("brightness",realLed.getBrightnessPercent());

		server.onUpdate("rgbValueBlue", (input) -> {
			Integer value = ContentHelper.ensureClass(input, Integer.class);
			log.info("setting blue value to " + value);
			realLed.setBlue((byte) value.intValue());
		});

		server.onUpdate("rgbValueRed", (input) -> {
			Integer value = ContentHelper.ensureClass(input, Integer.class);
			log.info("setting red value to " + value);
			realLed.setRed((byte) value.intValue());
		});

		server.onUpdate("rgbValueGreen", (input) -> {
			Integer value = ContentHelper.ensureClass(input, Integer.class);
			log.info("setting green value to " + value);
			realLed.setGreen((byte) value.intValue());
		});

		server.onUpdate("brightness", (input) -> {
			Integer value = ContentHelper.ensureClass(input, Integer.class);
			log.info("setting brightness to " + value);
			realLed.setBrightnessPercent(value.byteValue());
		});

		server.onUpdate("colorTemperature", (input) -> {
			Integer colorTemperature = ContentHelper.ensureClass(input, Integer.class);
			log.info("setting color temperature to " + colorTemperature +  " K");

			int red=  255;
			int green =  255;
			int blue =  255;

			int ct_scaled = colorTemperature / 100;

			if (ct_scaled > 66) {
				double fred = ct_scaled - 60;
				fred = 329.698727446 * Math.pow(fred, -0.1332047592);
				red = DemoLedAdapter.doubletoByte(fred);

				double fgreen = ct_scaled - 60;
				fgreen =  288.1221695283 * Math.pow(fgreen, -0.0755148492);
				green = DemoLedAdapter.doubletoByte(fgreen);
			} else {
				double fgreen = ct_scaled;
				fgreen = 99.4708025861 * Math.log(fgreen) - 161.1195681661;
				green = DemoLedAdapter.doubletoByte(fgreen);

				if(ct_scaled > 19) {
					double fblue = ct_scaled - 10;
					fblue = 138.5177312231 * Math.log(fblue) - 305.0447927307;
					blue = DemoLedAdapter.doubletoByte(fblue);
				}
			}

			log.info("color temperature equals (" + red + "," + green + "," + blue +")");
			server.setProperty("rgbValueGreen",green);
			server.setProperty("rgbValueRed", red);
			server.setProperty("rgbValueBlue", blue);

		});

		server.onInvoke("fadeIn", (input) -> {
			Integer duration = ContentHelper.ensureClass(input, Integer.class);
			log.info("fading in over {}s", duration);
			Runnable execution = new Runnable() {
				@Override
				public void run() {
					int steps = duration * 1000 / STEPLENGTH;
					int delta = Math.max(100 / steps, 1);

					int brightness = 0;
					server.setProperty("brightness", brightness);
					while (brightness < 100) {
						server.setProperty("brightness", brightness);
						try {
							Thread.sleep(STEPLENGTH);
						} catch (InterruptedException e) {
							break;
						}
						brightness += delta;
					}
				}
			};

			//TODO assign resource for thread (outside)
			new Thread(execution).start();

			return new Content("".getBytes(), MediaType.APPLICATION_JSON);
		});

		server.onInvoke("fadeOut", (input) -> {
			Integer duration = ContentHelper.ensureClass(input, Integer.class);
			Runnable execution = new Runnable() {
				@Override
				public void run() {
					int steps = duration * 1000 / STEPLENGTH;
					int delta = Math.max(100 / steps,1);

					int brightness = 100;
					server.setProperty("brightness", brightness);
					while(brightness > 0) {
						server.setProperty("brightness", brightness);
						try {
							Thread.sleep(STEPLENGTH);
						} catch (InterruptedException e) {
							break;
						}
						brightness -= delta;
					}
				}
			};

			new Thread(execution).start();

			return new Content("".getBytes(), MediaType.APPLICATION_JSON);
		});

		server.onInvoke("ledOnOff", (input) -> {
			Boolean target = ContentHelper.ensureClass(input, Boolean.class);

			if(target) {
				server.setProperty("rgbValueGreen",255);
				server.setProperty("rgbValueRed",255);
				server.setProperty("rgbValueBlue", 255);

				server.setProperty("brightness", 100);
			} else {
				server.setProperty("brightness", 0);

				server.setProperty("rgbValueGreen",0);
				server.setProperty("rgbValueRed",0);
				server.setProperty("rgbValueBlue", 0);
			}

			return new Content("".getBytes(), MediaType.APPLICATION_JSON);
		});

		server.onInvoke("trafficLight", (input) -> {
			Boolean go = ContentHelper.ensureClass(input, Boolean.class);
			log.info("trafic light changing state to {}",(go)? "green": "red" );

			if(go) {
				server.setProperty("rgbValueGreen",255);
				server.setProperty("rgbValueRed", 0);
				server.setProperty("rgbValueBlue", 0);
			} else {
				server.setProperty("rgbValueGreen",0);
				server.setProperty("rgbValueRed",255);
				server.setProperty("rgbValueBlue", 0);
			}

			return new Content("".getBytes(), MediaType.APPLICATION_JSON);
		} );
	}
}
