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

package de.thingweb.servient;

import de.thingweb.binding.coap.CoapBinding;
import de.thingweb.binding.http.HttpBinding;
import de.thingweb.security.TokenRequirements;
import de.thingweb.servient.impl.MultiBindingThingServer;
import de.thingweb.thing.Thing;

import java.io.IOException;


public final class ServientBuilder {
    private static final CoapBinding m_coapBinding = new CoapBinding();
    private static final HttpBinding m_httpBinding = new HttpBinding();

    private static boolean initialized = false;

    private ServientBuilder() {
        /* pure static class */
    }

    /**
     * Creates a new ThingServer for the specified thing model.
     *
     * @param thing the thing model, must not be null
     * @return the server, never null
     */
    public static ThingServer newThingServer(Thing thing) {
        return new MultiBindingThingServer(thing,
                m_coapBinding.getResourceBuilder(),
                m_httpBinding.getResourceBuilder()
        );
    }

    public static ThingServer newThingServer(TokenRequirements tokenRequirements, Thing thing) {
        return new MultiBindingThingServer(tokenRequirements, thing,
                m_coapBinding.getResourceBuilder(),
                m_httpBinding.getResourceBuilder()
        );
    }

    public static ThingServer newThingServer(TokenRequirements tokenRequirements) {
        return new MultiBindingThingServer( tokenRequirements,
                m_coapBinding.getResourceBuilder(),
                m_httpBinding.getResourceBuilder()
        );
    }

    public static void initialize() throws Exception {
        //am already initialized
        if(initialized) return;

        m_coapBinding.initialize();
        m_httpBinding.initialize();
        initialized = true;
    }

    public static void start() throws Exception {
        if(!initialized) initialize();
        m_coapBinding.start();
        m_httpBinding.start();
    }

    public static void stop() throws IOException {
        m_coapBinding.stop();
        m_httpBinding.stop();
    }

    public static ThingServer newThingServer() throws Exception {
        if(!initialized) initialize();
        return new MultiBindingThingServer(
                m_coapBinding.getResourceBuilder(),
                m_httpBinding.getResourceBuilder()
        );
    }
}
