package de.webthing.launcher;

import de.webthing.desc.DescriptionParser;
import de.webthing.servient.ServientBuilder;
import de.webthing.servient.ThingServer;
import de.webthing.thing.Thing;
import de.webthing.things.WotGreeter;

import java.io.File;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Launches a WoT thing.
 */
public class Launcher {

	public static void main(String[] args) throws Exception {
		ServientBuilder.initialize();

		String ledTD = "jsonld" + File.separator + "led.jsonld";
		
		Thing led = new Thing(DescriptionParser.fromFile(ledTD));
		
		ServientBuilder.newThingServer(led);

		ServientBuilder.start();
		
		System.in.read();
	}
}
