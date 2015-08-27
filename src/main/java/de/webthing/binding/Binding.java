package de.webthing.binding;

import de.webthing.thing.Thing;

public interface Binding {

	void setup();
	
	
	void bind(Thing thing);
}
