package de.thingweb.thing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 
 * Helper class to provide data structure to hold multiple values with the same key
 *
 * @author Victor Charpenay
 *
 */
public class Metadata
{
  
  private Map<String, List<JsonNode>> items = new HashMap<String, List<JsonNode>>();
  
  public void add(String key, JsonNode value) {
    checkKey(key);
    items.get(key).add(value);
  }
  
  public void add(String key, JsonNode... values) {
    checkKey(key);
    for (JsonNode v : values) {
      items.get(key).add(v);
    }
  }

  public void clear(String key) {
    if (items.containsKey(key)) {
      items.get(key).clear();
    }
  }
  
  /**
   * 
   * @param key key
   * @return either the value stored,
   * one single value if multiple values stored (not deterministic)
   * or null if key does not exist
   */
  public JsonNode get(String key) {
	  if(items.containsKey(key)) {
		  return items.get(key).iterator().next();
	  } else {
		  return null;
	  }
  }
  
  public List<JsonNode> getAll(String key) {
    return items.get(key);
 }
  
  public boolean contains(String key) {
    return items.containsKey(key);
  }
  
  private void checkKey(String key) {
    if (!items.containsKey(key)) {
      items.put(key, new ArrayList<JsonNode>());
    }
  }

}
