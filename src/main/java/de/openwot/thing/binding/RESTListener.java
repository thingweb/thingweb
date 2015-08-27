package de.openwot.thing.binding;


public interface RESTListener {

	String onGet();
	
	
	void onPut(String data);
	
	
	String onPost(String data);
	
	
	void onDelete();
}
