package de.openwot.thing.launcher;

import de.openwot.thing.things.WotGreeter;
import de.webthing.servient.ServientBuilder;


/**
 * Launches a WoT thing.
 */
public class Launcher {

	public static void main(String[] args) throws Exception {
		ServientBuilder.initialize();
		
		ServientBuilder.newThingServer(WotGreeter.newInstance("greeter"));
		
		ServientBuilder.start();
		
		System.in.read();
	}
}
