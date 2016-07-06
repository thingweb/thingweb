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

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.thingweb.binding.AbstractRESTListener;
import de.thingweb.binding.RESTListener;
import de.thingweb.binding.ResourceBuilder;
import de.thingweb.desc.ThingDescriptionParser;
import de.thingweb.security.SecurityTokenValidator;
import de.thingweb.security.SecurityTokenValidator4NicePlugfest;
import de.thingweb.security.TokenRequirements;
import de.thingweb.security.TokenRequirementsBuilder;
import de.thingweb.servient.Defines;
import de.thingweb.servient.ThingInterface;
import de.thingweb.servient.ThingServer;
import de.thingweb.thing.*;
import de.thingweb.util.encoding.ContentHelper;
import org.jose4j.lang.JoseException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ThreadFactory;
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
    protected SecurityTokenValidator4NicePlugfest validator;
    private TokenRequirements tokenRequirements;
    private final JsonNodeFactory jsonNodeFactory = new JsonNodeFactory(false);

    public MultiBindingThingServer(Thing thingModel,
                                   ResourceBuilder... bindings) {
        init(bindings);
        addThing(thingModel);
    }


    public MultiBindingThingServer(ResourceBuilder... bindings) {
        init(bindings);
    }

    public MultiBindingThingServer(TokenRequirements tokenRequirements, ResourceBuilder... bindings) {
        this.tokenRequirements = tokenRequirements;
        init(bindings);
    }

    public MultiBindingThingServer(TokenRequirements tokenRequirements,
                                   Thing thingModel,
                                   ResourceBuilder... bindings) {
        this.tokenRequirements = tokenRequirements;
        init(bindings);
        addThing(thingModel);
    }

    //Better move these urlize-methods to a helper class
    private static String urlizeTokens(String url) {
        return Arrays.stream(url.split("/"))
                .map(MultiBindingThingServer::urlize)
                .collect(Collectors.joining("/"));
    }

    private static String urlize(String name) {
        try {
            return URLEncoder.encode(name,"UTF-8").toLowerCase();
        } catch (UnsupportedEncodingException e) {
            return URLEncoder.encode(name).toLowerCase();
        }
    }

    protected void init(ResourceBuilder[] bindings) {
        Collections.addAll(m_bindings, bindings);
        m_bindings.forEach(resourceBuilder ->
                resourceBuilder.newResource(Defines.BASE_URL, new HypermediaIndex(
                                new HyperMediaLink("things", Defines.BASE_THING_URL)
                        )
                )
        );
    }

    protected SecurityTokenValidator getValidator() {
        if(validator == null) {
            if(tokenRequirements == null) {
                tokenRequirements = TokenRequirementsBuilder.createDefault();
            }
            try {
                validator = new SecurityTokenValidator4NicePlugfest(tokenRequirements);
            } catch (JoseException e) {
                throw new RuntimeException(e);
            }
        }
        return validator;
    }

    @Override
    public ThingInterface addThing(Thing thing) {
        if (null == thing) {
            throw new IllegalArgumentException("thingModel must not be null");
        }
        ServedThing servedThing = new ServedThing(thing);
        things.put(thing.getName(), servedThing);
        createBindings(servedThing, thing.isProtected());
        
        // update TD repository
        Thread t = new Thread(() -> {
            ServedThingRepository.updateTDRepository(thing);
        },"TdUpload" );
        t.start();
        
        return servedThing;
    }

    @Override
    public void rebindSec(String name, boolean enabled) {
        final ServedThing servedThing = things.get(name.toLowerCase());
        servedThing.getThingModel().setProtection(enabled);
        createBindings(servedThing,enabled);
    }

    @Override
    public void rebind(String name) {
        final ServedThing servedThing = things.get(name.toLowerCase());
        createBindings(servedThing,servedThing.getThingModel().isProtected());
    }

    @Override
    public ThingInterface getThing(String thingName) {
        return things.get(thingName);
    }

    @Override
    public Set<Thing> getThings() {
        Set<Thing> things = this.things.values().stream()
                .map(thing -> thing.getThingModel())
                .collect(Collectors.toSet());
        return things;
    }

    @Override
    public void setTokenRequirements(TokenRequirements tokenRequirements) {
        this.tokenRequirements = tokenRequirements;
        this.validator = null;
    }

    private void createBindings(ServedThing thingModel, boolean isProtected) {
        //TODO maybe replace the thins-root HypermediaIndex by a repository-output structure
        /* i.e.
            {
                "thing1" : <td of thing1>,
                "thing2" : <td of thing2>,
            }
         */

        AbstractRESTListener RepoRestListener = new AbstractRESTListener() {
            @Override
            public Content onGet() {
                final ObjectNode response = jsonNodeFactory.objectNode();
                things.forEach(
                        (name, thing) -> {
                                response.put(name, ThingDescriptionParser.toJsonObject(thing.getThingModel()));
                        }
                );
                return ContentHelper.wrap(response, MediaType.APPLICATION_JSON);
            }
        };

        // Hypermedia index
/*        final List<HyperMediaLink> thinglinks = things.keySet().stream()
                .sorted()
                .map(name -> new HyperMediaLink("thing", Defines.BASE_THING_URL + urlize(name)))
                .collect(Collectors.toList());

        final HypermediaIndex thingIndex = new HypermediaIndex(thinglinks);*/
        thingModel.getThingModel().getMetadata().clear("uris");

        // resources
        for (ResourceBuilder binding : m_bindings) {
            // update/create HATEOAS links to things
            binding.newResource(Defines.BASE_THING_URL, RepoRestListener);

            //add thing
            createBinding(binding, thingModel,isProtected);

            //update metadata
            thingModel.getThingModel().getMetadata()
                    .add("uris", binding.getBase() + Defines.BASE_THING_URL + urlize(thingModel.getName()));
        }
    }
    
    private void createBinding(ResourceBuilder resources, ServedThing servedThing, boolean isProtected) {
        final Thing thingModel = servedThing.getThingModel();

        final Map<String, RESTListener> interactionListeners = new HashMap<>();
        final String thingurl = Defines.BASE_THING_URL + thingModel.getName().toLowerCase();

        final ThingDescriptionRestListener tdRestListener = new ThingDescriptionRestListener(thingModel);

        // collect properties
        for (Property property : thingModel.getProperties()) {
            String url = thingurl + "/" + property.getName();

            final PropertyListener propertyListener = new PropertyListener(servedThing, property);
            if(isProtected) propertyListener.protectWith(getValidator());
            interactionListeners.put(url, propertyListener);
            if(property.getHrefs().size() < m_bindings.size())
                property.getHrefs().add(urlize(property.getName()));
//            TODO I'll comment this out until we have /value on the microcontroller
//            interactionListeners.put(url, new HypermediaIndex(
//                    new HyperMediaLink("value","value"),
//                    new HyperMediaLink("update","value","PUT","TBD")
//            ));

            interactionListeners.put(url + "/value", propertyListener);
        }

        // collect actions
        for (Action action : thingModel.getActions()) {
            //TODO optimize by preconstructing strings and using format
            final String url = thingurl + "/" + action.getName();
            final ActionListener actionListener = new ActionListener(servedThing, action);
            if(isProtected) actionListener.protectWith(getValidator());
            interactionListeners.put(url, actionListener);
            if(action.getHrefs().size() < m_bindings.size())
                action.getHrefs().add(urlize(action.getName()));
        }

        //add listener for thing description
        String tdUrl = thingurl + "/.td";
        interactionListeners.put(tdUrl, tdRestListener);

        // thing root
        resources.newResource(thingurl, tdRestListener);

        // leaves last (side-effect of coap-binding)
        interactionListeners.entrySet().stream().forEachOrdered(
                entry -> resources.newResource(entry.getKey(), entry.getValue())
        );

    }

}
