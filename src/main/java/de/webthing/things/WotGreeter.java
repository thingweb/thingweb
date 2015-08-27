package de.webthing.things;

import de.webthing.thing.Property;
import de.webthing.thing.Thing;

public final class WotGreeter {

	public static Thing newInstance(String name) {
		Thing thing = new Thing(name);
		
		thing.addProperty(new Property("message", true, false));
		thing.addProperty(new Property("customMessage", true, true));
		thing.addProperty(new Property("secretMessage", false, true));
		
		return thing;
	}
	
	
	private WotGreeter() {
		/* pure static class */
	}
}
