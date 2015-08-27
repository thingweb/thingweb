package de.openwot.thing.binding;

import de.webthing.thing.Thing;

public interface Binding {

	void setup();
	
	
	void bind(Thing thing);
}
