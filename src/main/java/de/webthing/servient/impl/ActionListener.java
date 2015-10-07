package de.webthing.servient.impl;

import de.webthing.binding.AbstractRESTListener;
import de.webthing.thing.Action;

import java.util.function.Function;

/**
 * Created by Johannes on 07.10.2015.
 */
class ActionListener extends AbstractRESTListener {

    private final Action action;
    private StateContainer m_state;

    public ActionListener(StateContainer m_state, Action action) {
        this.action = action;
        this.m_state = m_state;
    }

    @Override
	public byte[] onGet() {
		return ("Action: " + action.getName()).getBytes();
	}


    @Override
	public void onPut(byte[] data) {
		Function<?, ?> handler = m_state.getHandler(action);

		try {
			System.out.println("invoking " + action.getName());

			//TODO parsing and smart cast

			Function<byte[], byte[]> bytehandler = (Function<byte[], byte[]>) handler;
			bytehandler.apply(data);

		} catch (Exception e) {
            /*
* How do I return a 500?
*/
		}
	}

    @Override
	public byte[] onPost(byte[] data) {

		Function<?, ?> handler = m_state.getHandler(action);

		try {
			System.out.println("invoking " + action.getName());

			Function<byte[], byte[]> bytehandler = (Function<byte[], byte[]>) handler;
			bytehandler.apply(data);

		} catch (Exception e) {
/*
* How do I return a 500?
*/
			return "Error".getBytes();
		}

		return "OK".getBytes();
	}
}
