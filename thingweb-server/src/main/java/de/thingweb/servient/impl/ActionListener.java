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

package de.thingweb.servient.impl;

import de.thingweb.binding.AbstractRESTListener;
import de.thingweb.thing.Action;
import de.thingweb.thing.Content;
import de.thingweb.thing.MediaType;
import de.thingweb.util.encoding.ContentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Johannes on 07.10.2015.
 */
public class ActionListener extends AbstractRESTListener {

    private static final Logger log = LoggerFactory.getLogger(ActionListener.class);
    private final Action action;
    private final ServedThing servedThing;
    private final String inputType;

    public ActionListener(ServedThing servedThing, Action action) {
        this.action = action;
        this.servedThing = servedThing;
        this.inputType = this.action.getParams().get("parm");
    }

    @Override
    public Content onGet() {
        //TODO manage executions in statecontainer and add links to executions
        return HypermediaIndex.createContent(
                new HyperMediaLink("invoke","_self","POST",inputType),
                new HyperMediaLink("parent","../")
        );
    }

    @Override
    public void onPut(Content data) {
        log.warn("Action was called by PUT, which is a violation of the spec");
        Object param = ContentHelper.getValueFromJson(data);
        log.debug("invoking {}", action.getName());
        servedThing.invokeAction(action, param);
    }

    @Override
    public Content onPost(Content data) {
        Object param = null;
        if(data.getMediaType().equals(MediaType.APPLICATION_JSON)) {
            param = ContentHelper.getValueFromJson(data);
        } else if(data.getMediaType().equals(MediaType.TEXT_PLAIN)) {
            param = new String(data.getContent());
        }

        log.debug("invoking {} with {}", action.getName());
        Object response = servedThing.invokeAction(action, param);

        //differentiate if action has no outputvalue and return 204
        //else
        return ContentHelper.wrap(response, MediaType.APPLICATION_JSON);
    }
}
