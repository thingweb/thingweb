package de.webthing.binding;

import java.io.IOException;

public interface Binding {

	void initialize() throws IOException;

	ResourceBuilder getResourceBuilder();

	void start() throws IOException;
}
