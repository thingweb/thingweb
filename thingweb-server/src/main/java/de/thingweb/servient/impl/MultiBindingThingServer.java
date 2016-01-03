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

import com.fasterxml.jackson.databind.node.ObjectNode;
import de.thingweb.binding.AbstractRESTListener;
import de.thingweb.binding.RESTListener;
import de.thingweb.binding.ResourceBuilder;
import de.thingweb.desc.pojo.ThingDescription;
import de.thingweb.servient.Defines;
import de.thingweb.servient.ThingInterface;
import de.thingweb.servient.ThingServer;
import de.thingweb.thing.*;
import de.thingweb.util.encoding.ContentHelper;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;


/**
 * {@link ThingServer} implementation capable of offering a Thing via multiple
 * bindings simultaneously.
 */
public class MultiBindingThingServer implements ThingServer {

    /**
     * The logger.
     */
    protected final static Logger log = Logger.getLogger(MultiBindingThingServer.class.getCanonicalName());
    private final Map<String, ServedThing> things = new LinkedHashMap<>();
    private final Collection<ResourceBuilder> m_bindings = new ArrayList<>();


    public MultiBindingThingServer(Thing thingModel,
                                   ResourceBuilder... bindings) {

        for (ResourceBuilder b : bindings) {
            m_bindings.add(b);
        }

        addThing(thingModel);
    }


    public MultiBindingThingServer(ResourceBuilder... bindings) {
        Collections.addAll(m_bindings, bindings);
        m_bindings.forEach(resourceBuilder ->
                resourceBuilder.newResource(Defines.BASE_URL, new HypermediaIndex(
                                new HyperMediaLink("things", Defines.BASE_THING_URL)
                        )
                )
        );
    }

    @Override
    public ThingInterface addThing(Thing thing) {
        if (null == thing) {
            throw new IllegalArgumentException("thingModel must not be null");
        }
        ServedThing servedThing = new ServedThing(thing);
        things.put(thing.getName().toLowerCase(), servedThing);
        createBindings(servedThing);
        return servedThing;
    }

    @Override
    public ThingInterface addThing(ThingDescription thingDescription) {
        return addThing(new Thing(thingDescription));
    }

    @Override
    public ThingInterface getThing(String thingName) {
        return things.get(thingName.toLowerCase());
    }

    private void createBindings(ServedThing thingModel) {

        final List<HyperMediaLink> thinglinks = things.keySet().stream()
                .sorted()
                .map(name -> new HyperMediaLink("thing", Defines.BASE_THING_URL + name))
                .collect(Collectors.toList());

        final HypermediaIndex thingIndex = new HypermediaIndex(thinglinks);

        for (ResourceBuilder binding : m_bindings) {
            // update/create HATEOAS links to things
            binding.newResource(Defines.BASE_THING_URL, thingIndex);
            createBinding(binding, thingModel);
        }
    }

    private void createBinding(ResourceBuilder resources, ServedThing servedThing) {
        final Thing thingModel = servedThing.getThingModel();

        final Collection<Property> properties = thingModel.getProperties();
        final Collection<Action> actions = thingModel.getActions();

        final List<HyperMediaLink> interactionLinks = new LinkedList<>();
        final Map<String, RESTListener> interactionListeners = new HashMap<>();

        // collect properties
        for (Property property : properties) {
            String url = Defines.BASE_THING_URL + thingModel.getName() + "/" + property.getName();

            interactionListeners.put(url, new PropertyListener(servedThing, property));

//            TODO I'll comment this out until we have /value on the microcontroller
//            interactionListeners.put(url, new HypermediaIndex(
//                    new HyperMediaLink("value","value"),
//                    new HyperMediaLink("update","value","PUT","TBD")
//            ));

            interactionListeners.put(url + "/value", new PropertyListener(servedThing, property));
            interactionLinks.add(new HyperMediaLink("property", url));
        }

        // collect actions
        for (Action action : actions) {
            //TODO optimize by preconstructing strings and using format
            String url = Defines.BASE_THING_URL + thingModel.getName() + "/" + action.getName();
            interactionListeners.put(url, new ActionListener(servedThing, action));
            interactionLinks.add(new HyperMediaLink("action", url));
        }

        //add listener for thing description
        String tdUrl = Defines.BASE_THING_URL + thingModel.getName() + "/.td";
        interactionLinks.add(new HyperMediaLink("description",tdUrl));
        interactionListeners.put(tdUrl,
                new AbstractRESTListener() {
                    @Override
                    public Content onGet() {
                        ThingDescription td = thingModel.getThingDescription();

                        //manually adding the context
                        ObjectNode json = ContentHelper.getJsonMapper().valueToTree(td);
                        json.put("@context", "http://w3c.github.io/wot/w3c-wot-td-context.jsonld");

                        return ContentHelper.wrap(json,
                                MediaType.APPLICATION_JSON);
                    }
                });

        // thing root
        resources.newResource(Defines.BASE_THING_URL + thingModel.getName(),
                new HypermediaIndex(interactionLinks)
        );

        // leaves last (side-effect of coap-binding)
        interactionListeners.entrySet().stream().forEachOrdered(
                entry -> resources.newResource(entry.getKey(), entry.getValue())
        );

    }

}
