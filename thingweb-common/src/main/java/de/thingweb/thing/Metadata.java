package de.thingweb.thing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * Helper class to provide data structure to hold multiple values with the same key
 *
 * @author Victor Charpenay
 *
 */
public class Metadata
{
  
  private Map<String, List<String>> items = new HashMap<String, List<String>>();
  
  public void add(String key, String value) {
    checkKey(key);
    items.get(key).add(value);
  }
  
  public void add(String key, String... values) {
    checkKey(key);
    for (String v : values) {
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
  public String get(String key) {
	  if(items.containsKey(key)) {
		  return items.get(key).iterator().next();
	  } else {
		  return null;
	  }
  }
  
  public List<String> getAll(String key) {
    return items.get(key);
  }
  
  public boolean contains(String key) {
    return items.containsKey(key);
  }
  
  private void checkKey(String key) {
    if (!items.containsKey(key)) {
      items.put(key, new ArrayList<String>());
    }
  }

}
