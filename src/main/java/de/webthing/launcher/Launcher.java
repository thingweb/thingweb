package de.webthing.launcher;

import de.webthing.servient.ServientBuilder;
import de.webthing.things.WotGreeter;


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
