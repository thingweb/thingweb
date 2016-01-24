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

import org.junit.Test;

/**
 * Created by Johannes on 21.12.2015.
 */
public class WotJavaScriptRuntimeTest {


    @Test
    public void testRunFile() throws Exception {
        String script = TestTools.readResource("testwot.js");
        WotJavaScriptRuntime jsrt = WotJavaScriptRuntime.create();

        String testTD = TestTools.readResource("simplething.jsonld");
        jsrt.getEngine().put("testTD",testTD);

        jsrt.runScript(script);
    }

    public static void main(String[] args) throws Exception {
        //just done to avoid junit-sideffects
        WotJavaScriptRuntimeTest test = new WotJavaScriptRuntimeTest();
        //test.testRunFile();
    }

}