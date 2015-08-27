package de.openwot.thing.binding;

public interface ResourceBuilder {

	void newResource(String url, RESTListener restListener);
}
