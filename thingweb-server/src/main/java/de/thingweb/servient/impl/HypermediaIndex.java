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

package de.thingweb.servient.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import de.thingweb.binding.RESTListener;
import de.thingweb.thing.Content;
import de.thingweb.thing.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.Observer;

/**
 * Created by mchn1210 on 20.10.2015.
 */
public class HypermediaIndex implements RESTListener {

    private static final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    private static final ObjectMapper mapper = new ObjectMapper();

    private Content myContent;

    public HypermediaIndex(List<HyperMediaLink> links) {
        myContent = createContent(links);
    }

    public HypermediaIndex(HyperMediaLink... link) {
        myContent = createContent(Arrays.asList(link));
    }

    private Content createContent(List<HyperMediaLink> links) {
        String json = null;
        try {
            json = ow.writeValueAsString(links);
        } catch (JsonProcessingException e) {
            json = "{ \"error\" : \" " + e.getMessage() + "\"  }";
        }
        return new Content(json.getBytes(),MediaType.APPLICATION_JSON);
    }

    @Override
    public Content onGet() throws UnsupportedOperationException, RuntimeException {
        return myContent;
    }

    @Override
    public void onPut(Content data) throws UnsupportedOperationException, IllegalArgumentException, RuntimeException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Content onPost(Content data) throws UnsupportedOperationException, IllegalArgumentException, RuntimeException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void onDelete() throws UnsupportedOperationException, RuntimeException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void addObserver(Observer o) {
        //ignored
    }
}
