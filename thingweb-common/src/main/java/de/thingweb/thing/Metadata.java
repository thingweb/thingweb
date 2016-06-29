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
  private List<HyperMediaLink> associations = new ArrayList<>();
  
  public List<HyperMediaLink> getAssociations(){
	  return associations;
  }
 
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
  
  public void remove(String key){
	  if(items.containsKey(key))
		  items.remove(key);
  }
  
  /**
   * 
   * @param key key
   * @return either the value stored,
   * one single value if multiple values stored (not deterministic)
   * or null if key does not exist
   */
  public String get(String key) {
    return items.get(key).get(0);
  }
  
  public List<String> getAll(String key) {
    return items.get(key);
  }
  
  public boolean contains(String key) {
    return items.containsKey(key);
  }
  
  public Map<String, List<String>> getItems(){
	  return items;
  }
  
  private void checkKey(String key) {
    if (!items.containsKey(key)) {
      items.put(key, new ArrayList<String>());
    }
  }
  
}
