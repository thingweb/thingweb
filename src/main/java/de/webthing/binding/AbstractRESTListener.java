package de.webthing.binding;

public class AbstractRESTListener implements RESTListener {

	@Override
	public byte[] onGet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onPut(byte[] data) throws UnsupportedOperationException, IllegalArgumentException, RuntimeException {
		throw new UnsupportedOperationException();
	}

	@Override
	public byte[] onPost(byte[] data) throws UnsupportedOperationException, IllegalArgumentException, RuntimeException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onDelete() {
		throw new UnsupportedOperationException();
	}
}
