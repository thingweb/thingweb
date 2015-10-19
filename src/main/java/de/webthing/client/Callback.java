package de.webthing.client;

import de.webthing.thing.Content;

public interface Callback {
	
	public void onGet(String propertyName, Content response);
	
	public void onGetError(String propertyName);
	
	public void onPut(String propertyName, Content response);
	
	public void onPutError(String propertyName);
	
	public void onObserve(String propertyName, Content response);
	
	public void onObserveError(String propertyName);
	
	public void onAction(String actionName, Content response);
	
	public void onActionError(String actionName);

}
