package de.webthing.servient.impl;

import de.webthing.binding.AbstractRESTListener;
import de.webthing.thing.Action;
import de.webthing.thing.Content;
import de.webthing.thing.MediaType;

import java.util.function.Function;

/**
 * Created by Johannes on 07.10.2015.
 */
public class ActionListener extends AbstractRESTListener {

    private final Action action;
    private StateContainer m_state;

    public ActionListener(StateContainer m_state, Action action) {
        this.action = action;
        this.m_state = m_state;
    }

    @Override
	public Content onGet() {
    	return new Content(("Action: " + action.getName()).getBytes(), MediaType.TEXT_PLAIN);
	}


    @Override
	public void onPut(Content data) {
		Function<?, ?> handler = m_state.getHandler(action);

		try {
			System.out.println("invoking " + action.getName());

			//TODO parsing and smart cast

			Function<Content, Content> bytehandler = (Function<Content, Content>) handler;
			bytehandler.apply(data);

		} catch (Exception e) {
            /*
* How do I return a 500?
*/
		}
	}

    @Override
	public Content onPost(Content data) {

		Function<?, ?> handler = m_state.getHandler(action);
		System.out.println("invoking " + action.getName());

		//TODO parsing and smart cast

		Function<Content, Content> bytehandler = (Function<Content, Content>) handler;
		bytehandler.apply(data);

		return new Content("OK".getBytes(), MediaType.TEXT_PLAIN);
	}
}
