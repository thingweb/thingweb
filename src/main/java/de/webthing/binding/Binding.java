package de.webthing.binding;

import de.webthing.thing.Thing;

import java.io.IOException;

public interface Binding {

	void initialize() throws IOException;

	ResourceBuilder getResourceBuilder();

	void start() throws IOException;
}
