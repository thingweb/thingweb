package de.thingweb.jsruntime;

import java.util.function.Consumer;

/**
 * Created by Johannes on 09.12.2015.
 */

// naiive ES6-like promise implementation for nashorn (do futures work there?)
public class JsPromise {
    private Consumer onSuccess;
    private Consumer onError;

    public void resolve(Object param) {
        if (onSuccess != null)
            onSuccess.accept(param);
    }

    public void reject(Object error) {
        if (onError != null) {
            onError.accept(error);
        }
    }

    public JsPromise then(Consumer onSuccessCallBack) {
        onSuccess = onSuccessCallBack;
        return this;
    }

    public JsPromise then(JsPromise chainedPromise) {
        onSuccess.andThen(chainedPromise.onSuccess);
        return this;
    }

    //cannot be named catch
    public JsPromise _catch(Consumer onErrorCallBack) {
        onError = onErrorCallBack;
        return this;
    }
}
