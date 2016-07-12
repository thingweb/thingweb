package de.thingweb.thing;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 
 * Helper class to provide data structure to hold metadata values
 *
 * @author Victor Charpenay
 * @author https://github.com/danielpeintner
 *
 */
public class Metadata {

	private Map<String, JsonNode> items = new HashMap<String, JsonNode>();

	public void add(String key, JsonNode value) {
		items.put(key, value);
	}

	public void clear(String key) {
		items.remove(key);
	}

	/**
	 * 
	 * @param key
	 *            key
	 * @return the value stored
	 */
	public JsonNode get(String key) {
		return items.get(key);
	}

	public boolean contains(String key) {
		return items.containsKey(key);
	}

}
