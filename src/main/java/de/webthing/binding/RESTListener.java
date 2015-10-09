package de.webthing.binding;

import de.webthing.thing.Content;

public interface RESTListener {

	Content onGet() throws UnsupportedOperationException, RuntimeException;

	void onPut(Content data) throws UnsupportedOperationException, IllegalArgumentException, RuntimeException;
	
	Content onPost(Content data) throws UnsupportedOperationException, IllegalArgumentException, RuntimeException;
	
	void onDelete() throws UnsupportedOperationException,  RuntimeException;
}
