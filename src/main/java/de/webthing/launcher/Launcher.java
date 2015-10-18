package de.webthing.launcher;

import de.webthing.desc.DescriptionParser;
import de.webthing.leddemo.DemoLed;
import de.webthing.leddemo.DemoLedAdapter;
import de.webthing.servient.ServientBuilder;
import de.webthing.servient.ThingServer;
import de.webthing.thing.Thing;
import de.webthing.things.WotGreeter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;


/**
 * Launches a WoT thing.
 */
public class Launcher {

	private static final Logger log = LoggerFactory.getLogger(Launcher.class);

	public static void main(String[] args) throws Exception {
		ServientBuilder.initialize();

		String ledTD = "jsonld" + File.separator + "led.jsonld";

		Thing led = new Thing(DescriptionParser.fromFile(ledTD));
		ThingServer server = ServientBuilder.newThingServer(led);

		DemoLed realLed = new DemoLedAdapter();

		server.onUpdate("rgbValueBlue", (nV) -> {
			//byte blueValue = nV.getContent().toNumber();
			log.info("setting blue value to {}", nV.getContent().toString());
			byte blueValue = (byte) 255;
			realLed.setBlue(blueValue);
		});

		server.onInvoke("fadeIn", (secs) -> {
			String msg = "I am fading out over " + secs + "  s...";
			System.out.println(msg);
			return msg;
		});
		server.onInvoke("fadeOut", (secs) -> {	String msg = "I am fading out over " + secs + "  s..."; System.out.println(msg); return msg;  });


		ServientBuilder.start();
		
		System.in.read();
	}
}
