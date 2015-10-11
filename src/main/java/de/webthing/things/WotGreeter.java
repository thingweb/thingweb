package de.webthing.things;

import de.webthing.thing.Action;
import de.webthing.thing.Property;
import de.webthing.thing.Thing;

public final class WotGreeter {

	public static Thing newInstance(String name) {
		Thing thing = new Thing(name);
		
		thing.addProperty(Property.getBuilder("message").build());
		thing.addProperty(Property.getBuilder("customMessage").setWriteable(true).build());
		thing.addProperty(Property.getBuilder("secretMessage").setReadable(false).setWriteable(true).build());

		thing.addAction(Action.getBuilder("selftest").build());

		return thing;
	}
	
	
	private WotGreeter() {
		/* pure static class */
	}
}
