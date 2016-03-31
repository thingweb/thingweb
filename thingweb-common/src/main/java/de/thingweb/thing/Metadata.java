package de.thingweb.thing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
  
  /**
   * 
   * @param key
   * @return either the value stored,
   * one single value if multiple values stored (not deterministic)
   * or null if key does not exist
   */
  public String get(String key) {
    return items.get(items).iterator().next();
  }
  
  public List<String> getAll(String key) {
    return items.get(key);
  }
  
  private void checkKey(String key) {
    if (!items.containsKey(key)) {
      items.put(key, new ArrayList<String>());
    }
  }

}
