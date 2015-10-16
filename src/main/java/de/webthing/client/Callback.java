package de.webthing.client;

public interface Callback {
	
	public void onGet(String propertyName, String response);
	
	public void onGetError(String propertyName);
	
	public void onPut(String propertyName, String response);
	
	public void onPutError(String propertyName);
	
	public void onObserve(String propertyName, String response);
	
	public void onObserveError(String propertyName);
	
	public void onAction(String actionName, String response);
	
	public void onActionError(String actionName);

}
