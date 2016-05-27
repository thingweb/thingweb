/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Siemens AG and the thingweb community
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package de.thingweb.servient;

import de.thingweb.thing.Action;
import de.thingweb.thing.Property;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 * The ThingInterface provides methods for interacting with things.
 */
public interface ThingInterface {
    /**
     * <p>Sets a property to a new value.</p>
     * <p>
     * This method will throw {@link IllegalArgumentException}s if the property
     * is invalid (e.g. does not belong to the Thing served by this server) or
     * the value is incompatible with the specified property.</p>
     *
     * @param property the property, must not be null
     * @param value    the new value must not be null
     */
    void setProperty(Property property, Object value);

    /**
     * <p>Sets a property by name.</p>
     * <p>
     * This method will throw an {@link IllegalArgumentException} if no
     * property with the given name exists.</p>
     * <p>
     * This method will throw an {@link IllegalArgumentException} if the
     * property is invalid (e.g. does not belong to the Thing served by this
     * server) or the value is incompatible with the specified property.</p>
     *
     * @param propertyName name of the property to set, must not be null
     * @param value        the new value must not be null
     */
    void setProperty(String propertyName, Object value);

    void updateProperty(Property property, Object value);

    Object getProperty(Property property);

    Object getProperty(String propertyName);

    Object invokeAction(String actionName, Object parameter);

    Object invokeAction(Action action, Object parameter);

    @Deprecated
    void onInvoke(String actionName, Function<Object, Object> callback);

    void onActionInvoke(String actionName, Function<Object, Object> callback);

    void onPropertyUpdate(String propertyName, Consumer<Object> callback);

    @Deprecated
    void onUpdate(String propertyName, Consumer<Object> callback);

    void onPropertyRead(Consumer<Object> callback);
    
    void onPropertyUpdate(BiConsumer<Object, Object> callback);
    
    void onActionInvoke(BiFunction<Object, Object, Object> callback);

    String getName();
    
    List<String> getURIs();
}
