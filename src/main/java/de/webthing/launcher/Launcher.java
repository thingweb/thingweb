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

package de.webthing.launcher;

import de.webthing.desc.DescriptionParser;
import de.webthing.leddemo.DemoLedAdapter;
import de.webthing.servient.ServientBuilder;
import de.webthing.servient.ThingServer;
import de.webthing.thing.Content;
import de.webthing.thing.MediaType;
import de.webthing.thing.Thing;
import de.webthing.util.encoding.ContentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;


/**
 * Launches a WoT thing.
 */
public class Launcher {

	private static final Logger log = LoggerFactory.getLogger(Launcher.class);
	private static final int STEPLENGTH = 100;

	private static <T> T getValue (Content c, Class<T> clazz) {
		Map map = (Map) ContentHelper.parse(c, Map.class);
		Object o = map.get("value");

		try {
			return clazz.cast(o);
		} catch(ClassCastException e) {
			String msg = String.format(
					"expected value to be of type %s, not %s in %s",
					clazz,
					o.getClass(),
					new String(c.getContent())
					);
			log.warn(msg);
			throw new IllegalArgumentException(msg);
		}
	}

	public static void main(String[] args) throws Exception {
		ServientBuilder.initialize();

		String ledTD = "jsonld" + File.separator + "fancy_led.jsonld";

		Thing led = new Thing(DescriptionParser.fromFile(ledTD));
		ThingServer server = ServientBuilder.newThingServer(led);

		DemoLedAdapter realLed = new DemoLedAdapter();

		server.onUpdate("rgbValueBlue", (input) -> {
			Integer value = getValue(input, Integer.class);
			log.info("setting blue value to " + value);
			realLed.setBlue((byte) value.intValue());
		});

		server.onUpdate("rgbValueRed", (input) -> {
			Integer value = getValue(input, Integer.class);
			log.info("setting red value to " + value);
			realLed.setRed((byte) value.intValue());
		});

		server.onUpdate("rgbValueGreen", (input) -> {
			Integer value = getValue(input, Integer.class);
			log.info("setting green value to " + value);
			realLed.setGreen((byte) value.intValue());
		});

		server.onUpdate("brightness", (input) -> {
			Integer value = getValue(input, Integer.class);
			log.info("setting brightness to " + value);
			realLed.setBrightnessPercent(value.byteValue());
		});

		server.onUpdate("colorTemperature", (input) -> {
			Integer value = getValue(input, Integer.class);
			log.info("setting temperature to " + value);

			realLed.setColorTemperature(value);
		});

		server.onInvoke("fadeIn", (input) -> {
			Integer duration = getValue(input, Integer.class);
			log.info("fading in over {}s",duration);
			Runnable execution = new Runnable() {
				@Override
				public void run() {
					int steps = duration * 1000 / STEPLENGTH;
					int delta = Math.max(100 / steps,1);

					short brightness = 0;
					realLed.setBrightnessPercent(brightness);
					while(brightness < 100) {
						realLed.setBrightnessPercent(brightness);
						brightness += delta;
					}
				}
			};

			//TODO assign resource for thread (outside)
			new Thread(execution).run();

			return new Content("".getBytes(), MediaType.APPLICATION_JSON);
		});

		server.onInvoke("fadeOut", (input) -> {
			Integer duration = getValue(input, Integer.class);
			Runnable execution = new Runnable() {
				@Override
				public void run() {
					int steps = duration * 1000 / STEPLENGTH;
					int delta = Math.max(100 / steps,1);

					short brightness = 100;
					realLed.setBrightnessPercent(brightness);
					while(brightness > 0) {
						realLed.setBrightnessPercent(brightness);
						brightness -= delta;
					}
				}
			};

			new Thread(execution).run();

			return new Content("".getBytes(), MediaType.APPLICATION_JSON);
		});

		server.onInvoke("ledOnOff", (input) -> {
			Boolean target = getValue(input, Boolean.class);

			if(target) {
				realLed.setBlue((byte) 255);
				realLed.setGreen((byte) 255);
				realLed.setRed((byte) 255);
				realLed.setBrightnessPercent((short) 100);
			} else {
				realLed.setBrightnessPercent((short) 0);
			}

			return new Content("".getBytes(), MediaType.APPLICATION_JSON);
		});



		ServientBuilder.start();
		}
	}
