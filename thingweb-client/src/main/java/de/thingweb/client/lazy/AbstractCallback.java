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

package de.thingweb.client.lazy;

import de.thingweb.client.Callback;
import de.thingweb.thing.Content;

/**
 * Created by Johannes on 21.12.2015.
 * Abstract base useful to override when you just need one callback-pair
 *
 */
public class AbstractCallback implements Callback {

    public static final String MESSAGE = "unexpected %s callback: %s on %s";

    @Override
    public void onGet(String propertyName, Content response) {
        throwEx("GET",propertyName,false);
    }

    @Override
    public void onGetError(String propertyName) {
        throwEx("GET",propertyName,true);
    }

    private static void throwEx(String method, String target,boolean error) {
        throw new RuntimeException(String.format(MESSAGE,error? "error" : "success",method,target));
    }

    @Override
    public void onPut(String propertyName, Content response) {
        throwEx("PUT",propertyName,false);
    }

    @Override
    public void onPutError(String propertyName, String message) {
        throwEx("PUT",propertyName,true);
    }

    @Override
    public void onObserve(String propertyName, Content response) {
        throwEx("GET+OBSERVE",propertyName,false);
    }

    @Override
    public void onObserveError(String propertyName) {
        throwEx("GET+OBSERVE",propertyName,true);
    }

    @Override
    public void onAction(String actionName, Content response) {
        throwEx("POST",actionName,false);
    }

    @Override
    public void onActionError(String actionName) {
        throwEx("POST",actionName,true);
    }
}
