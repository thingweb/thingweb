package de.webthing.servient;

import de.webthing.thing.Content;
import de.webthing.thing.Property;

import java.util.concurrent.Callable;
import java.util.function.Function;


/**
 * The ThingInterface provides methods for interacting with things.
 */
public interface ThingInterface {
	/**
	 * Sets a property to a new value.<p>
	 * 
	 * This method will throw {@link IllegalArgumentException}s if the property
	 * is invalid (e.g. does not belong to the Thing served by this server) or
	 * the value is incompatible with the specified property.
	 * 
	 * @param property the property, must not be null
	 * @param value the new value must not be null
	 */
	void setProperty(Property property, Content value);
	
	
	/**
	 * Sets a property by name.<p>
	 * 
	 * This method will throw an {@link IllegalArgumentException} if no 
	 * property with the given name exists.<p>
	 * 
	 * This method will throw an {@link IllegalArgumentException} if the 
	 * property is invalid (e.g. does not belong to the Thing served by this 
	 * server) or the value is incompatible with the specified property.
	 * 
	 * @param propertyName name of the property to set, must not be null
	 * @param value the new value must not be null
	 */
	void setProperty(String propertyName, Content value);
	
	
	Content getProperty(Property property);
	
	
	Content getProperty(String propertyName);


}
