package de.webthing.thing;


/**
 * ModelListeners can be used to get a notification whenever the model of a
 * thing changes.<p>
 * 
 * ModelListeners can be registered at {@link Thing}s.
 */
public interface ModelListener {
	/**
	 * Called whenever the {@link Thing} changes.<p>
	 * 
	 * @param thing the thing, never null
	 */
	void onChange(Thing thing);
}
