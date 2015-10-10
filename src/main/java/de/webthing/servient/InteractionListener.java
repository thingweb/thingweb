package de.webthing.servient;


import de.webthing.servient.impl.MultiBindingThingServer;

/**
 * Called for each interaction with a ThingServer, e.g., reading or writing of 
 * values, invocation of actions, etc.
 */
public interface InteractionListener {
	/**
	 * Called whenever a property is read.<p>
	 * 
	 * This method is called before the value of the property is read by the
	 * ThingServer. It is therefore possible to modify the value returned 
	 * to the client from this callback, e.g., by calling 
	 * {@link ThingServer#setProperty(String, Object)}. However there is no
	 * guarantee that the client will see exactly this value as there might
	 * be multiple concurrent callback invocations.<p>
	 * 
	 * A typical scenarios where such behavior is acceptable is the on-demand
	 * acquisition of sensor readings. In this case there is no problem if the
	 * client sees a value written from another callback as this is similarly
	 * fresh as the value acquired by this callback.
	 * 
	 * @param thingServer the server affected by the interaction, never null
	 */
	void onReadProperty(ThingServer thingServer);

	void onWriteProperty(ThingServer thingServer);
}
