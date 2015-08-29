package de.webthing.launcher;

import de.webthing.servient.ServientBuilder;
import de.webthing.servient.ThingServer;
import de.webthing.things.WotGreeter;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Launches a WoT thing.
 */
public class Launcher {

	public static void main(String[] args) throws Exception {
		ServientBuilder.initialize();

		ThingServer server = ServientBuilder.newThingServer(WotGreeter.newInstance("greeter"));

		Random rnd = new Random();

		TimerTask valueChanger = new TimerTask() {
			@Override
			public void run() {
				server.setProperty("message", "Hello number " + rnd.nextInt(100));
			}
		};

		Timer tiktak = new Timer(true);
		tiktak.schedule(valueChanger, 500,500);

		ServientBuilder.start();
		
		System.in.read();
		tiktak.cancel();
	}
}
