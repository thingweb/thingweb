/*
 *
 *  * The MIT License (MIT)
 *  *
 *  * Copyright (c) 2016 Siemens AG and the thingweb community
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in
 *  * all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  * THE SOFTWARE.
 *
 */

package de.thingweb.jsruntime;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Created by Johannes on 09.12.2015.
 */

// naiive ES6-like promise implementation for nashorn
//see https://www.youtube.com/watch?v=kVyVyRdxwxE&feature=youtu.be&t=1h17m37s
//slides 68++
//intended only for proof-of-concepts, DO NOT USE IN PRODUCTION

public class JsPromise {
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private CompletableFuture future = new CompletableFuture();
    private Consumer onSuccess;
    private Consumer onError;

    public void resolve(Object param) {
        future.complete(param);
    }

    public void reject(Object error) {
        future.completeExceptionally(new RuntimeException(error.toString()));
    }

    public JsPromise then(Consumer onSuccessCallBack) {
        //future.thenAccept(onSuccessCallBack);
        onSuccess = onSuccessCallBack;
        executor.submit(() -> {
            try {
                onSuccessCallBack.accept(future.get());
            } catch (InterruptedException e) {
               throw new RuntimeException(e);
            } catch (ExecutionException e) {
                onError.accept(e);
            }
        });

        return this;
    }

    //this is not really right according to the A+-contract, is should feed the result of onsuccess
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
