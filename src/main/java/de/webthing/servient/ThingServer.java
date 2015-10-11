package de.webthing.servient;


import de.webthing.thing.Content;

import java.util.function.Consumer;
import java.util.function.Function;

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

	//TODO decide whether to announce unsuccessful invocation by exception or retval
	void onInvoke(String actionName, Function<Object, Object> callback);

	void onUpdate(String propertyName, Consumer<Content> callback);
}
