package de.openwot.thing.binding;

public class AbstractRESTListener implements RESTListener {

	@Override
	public String onGet() {
		throw new UnsupportedOperationException();
	}

	
	@Override
	public void onPut(String data) {
		throw new UnsupportedOperationException();
	}

	
	@Override
	public String onPost(String data) {
		throw new UnsupportedOperationException();
	}

	
	@Override
	public void onDelete() {
		throw new UnsupportedOperationException();
	}
}
