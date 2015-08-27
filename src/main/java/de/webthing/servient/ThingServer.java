package de.webthing.servient;


/**
 * The ThingServer is thread safe.
 */
public interface ThingServer extends ThingInterface {
	/**
	 * Adds an InteractionListener to this server.
	 * 
	 * @param listener the listener to add, must not be null
	 */
	void addInteractionListener(InteractionListener listener);
}
