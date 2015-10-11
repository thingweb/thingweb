package de.webthing.binding;

import de.webthing.thing.Content;

public class AbstractRESTListener implements RESTListener {

	@Override
	public Content onGet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onPut(Content data) throws UnsupportedOperationException, IllegalArgumentException, RuntimeException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Content onPost(Content data) throws UnsupportedOperationException, IllegalArgumentException, RuntimeException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onDelete() {
		throw new UnsupportedOperationException();
	}
}
