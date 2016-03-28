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

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.thingweb.binding.AbstractRESTListener;
import de.thingweb.binding.RESTListener;
import de.thingweb.binding.ResourceBuilder;
import de.thingweb.desc.pojo.Protocol;
import de.thingweb.desc.pojo.ThingDescription;
import de.thingweb.security.SecurityTokenValidator;
import de.thingweb.security.SecurityTokenValidator4NicePlugfest;
import de.thingweb.security.TokenRequirements;
import de.thingweb.security.TokenRequirementsBuilder;
import de.thingweb.servient.Defines;
import de.thingweb.servient.ThingInterface;
import de.thingweb.servient.ThingServer;
import de.thingweb.thing.*;
import de.thingweb.util.encoding.ContentHelper;
import javafx.util.Pair;

import org.jose4j.lang.JoseException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
    protected SecurityTokenValidator4NicePlugfest validator;
    private TokenRequirements tokenRequirements;

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
        things.put(thing.getName().toLowerCase(), servedThing);
        createBindings(servedThing, thing.isProtected());
        return servedThing;
    }

    @Override
    public void rebindSec(String name, boolean enabled) {
        final ServedThing servedThing = things.get(name.toLowerCase());
        servedThing.getThingModel().setProtection(enabled);
        createBindings(servedThing,enabled);
    }

    @Override
    public ThingInterface addThing(ThingDescription thingDescription) {
        return addThing(new Thing(thingDescription));
    }

    @Override
    public ThingInterface getThing(String thingName) {
        return things.get(thingName.toLowerCase());
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
        final List<HyperMediaLink> thinglinks = things.keySet().stream()
                .sorted()
                .map(name -> new HyperMediaLink("thing", Defines.BASE_THING_URL + urlize(name)))
                .collect(Collectors.toList());

        final HypermediaIndex thingIndex = new HypermediaIndex(thinglinks);

        final List<String> protocols = thingModel.getThingModel().getThingDescription().getMetadata().getProtocols();

        int prio=1;
        for (ResourceBuilder binding : m_bindings) {
            // update/create HATEOAS links to things
            binding.newResource(Defines.BASE_THING_URL, thingIndex);
            createBinding(binding, thingModel,isProtected);
            final Protocol protocol = new Protocol(binding.getBase() + Defines.BASE_THING_URL + urlize(thingModel.getName()),prio++);
            protocols.add(protocol.uri);
        }


    }
    
    private void createBinding(ResourceBuilder resources, ServedThing servedThing, boolean isProtected) {
        final Thing thingModel = servedThing.getThingModel();

        final Collection<Property> properties = thingModel.getProperties();
        final Collection<Action> actions = thingModel.getActions();

        final List<HyperMediaLink> interactionLinks = new LinkedList<>();
        final Map<String, RESTListener> interactionListeners = new HashMap<>();
        final String thingurl = Defines.BASE_THING_URL + thingModel.getName().toLowerCase();

        // collect properties
        for (Property property : properties) {
            String url = thingurl + "/" + property.getDescription().getName();

            final PropertyListener propertyListener = new PropertyListener(servedThing, property);
            if(isProtected) propertyListener.protectWith(getValidator());
            interactionListeners.put(url, propertyListener);

//            TODO I'll comment this out until we have /value on the microcontroller
//            interactionListeners.put(url, new HypermediaIndex(
//                    new HyperMediaLink("value","value"),
//                    new HyperMediaLink("update","value","PUT","TBD")
//            ));

            interactionListeners.put(url + "/value", propertyListener);
            interactionLinks.add(new HyperMediaLink("property", urlizeTokens(url)));
        }

        // collect actions
        for (Action action : actions) {
            //TODO optimize by preconstructing strings and using format
            final String url = thingurl + "/" + action.getName();
            final ActionListener actionListener = new ActionListener(servedThing, action);
            if(isProtected) actionListener.protectWith(getValidator());
            interactionListeners.put(url, actionListener);
            interactionLinks.add(new HyperMediaLink("action", urlizeTokens(url)));
        }

        //add listener for thing description
        String tdUrl = thingurl + "/.td";
        interactionLinks.add(new HyperMediaLink("description",urlizeTokens(tdUrl)));
        interactionListeners.put(tdUrl,
                new AbstractRESTListener() {
                    @Override
                    public Content onGet() {
                        //TODO fill up metadata
                        ThingDescription td = thingModel.getThingDescription();

                        //manually adding the context
                        ObjectNode json = ContentHelper.getJsonMapper().valueToTree(td);
                        ArrayNode contextNode = json.putArray("@context");
                        contextNode.add(ThingDescription.WOT_TD_CONTEXT);
                        if(td.getAdditionalContexts() != null){
	                        for(Pair<String,String> contextEntry : td.getAdditionalContexts()){
	                        	ObjectNode on = ContentHelper.getJsonMapper().createObjectNode();
	                        	on.put(contextEntry.getKey(), contextEntry.getValue());
	                        	contextNode.add(on);
	                        }
                        }

                        //json.put("@context", "http://w3c.github.io/wot/w3c-wot-td-context.jsonld");

                        return ContentHelper.wrap(json,
                                MediaType.APPLICATION_JSON);
                    }
                });

        // thing root
        resources.newResource(thingurl,
                new HypermediaIndex(interactionLinks)
        );

        // leaves last (side-effect of coap-binding)
        interactionListeners.entrySet().stream().forEachOrdered(
                entry -> resources.newResource(entry.getKey(), entry.getValue())
        );

    }

}
