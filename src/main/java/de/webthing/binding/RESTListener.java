package de.webthing.binding;


public interface RESTListener {

	byte[] onGet() throws UnsupportedOperationException, RuntimeException;

	void onPut(byte[] data) throws UnsupportedOperationException, IllegalArgumentException, RuntimeException;
	
	
	byte[] onPost(byte[] data) throws UnsupportedOperationException, IllegalArgumentException, RuntimeException;
	
	
	void onDelete() throws UnsupportedOperationException,  RuntimeException;
}
