package de.thingweb.thing;

import java.util.HashMap;
import java.util.Map;

public class ThingMetadata extends Metadata {

	public final static String METADATA_ELEMENT_URIS = "uris";
	public final static String METADATA_ELEMENT_ENCODINGS = "encodings";

	private Map<String, String> contexts = new HashMap<String, String>();

	public void addContext(String key, String value) {
		contexts.put(key, value);
	}

	public Map<String, String> getContexts() {
		return contexts;
	}

}
