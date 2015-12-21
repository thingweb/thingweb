package de.thingweb.client.lazy;

import de.thingweb.client.Callback;
import de.thingweb.thing.Content;

/**
 * Created by Johannes on 21.12.2015.
 * Abstract base useful to override when you just need one callback-pair
 *
 */
public class AbstractCallback implements Callback {

    @Override
    public void onGet(String propertyName, Content response) {
        throw new RuntimeException("unexpected callback");
    }

    @Override
    public void onGetError(String propertyName) {
        throw new RuntimeException("unexpected callback");
    }

    @Override
    public void onPut(String propertyName, Content response) {
        throw new RuntimeException("unexpected callback");
    }

    @Override
    public void onPutError(String propertyName) {
        throw new RuntimeException("unexpected callback");
    }

    @Override
    public void onObserve(String propertyName, Content response) {
        throw new RuntimeException("unexpected callback");
    }

    @Override
    public void onObserveError(String propertyName) {
        throw new RuntimeException("unexpected callback");
    }

    @Override
    public void onAction(String actionName, Content response) {
        throw new RuntimeException("unexpected callback");
    }

    @Override
    public void onActionError(String actionName) {
        throw new RuntimeException("unexpected callback");
    }
}
