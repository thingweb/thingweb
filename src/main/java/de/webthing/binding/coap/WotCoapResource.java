package de.webthing.binding.coap;

import de.webthing.binding.RESTListener;
import de.webthing.binding.auth.TokenVerifier;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.util.Collections;
import java.util.List;

import static org.eclipse.californium.core.coap.MediaTypeRegistry.TEXT_PLAIN;

/**
 * Created by Johannes on 05.10.2015.
 */
public class WotCoapResource extends CoapResource {
    private final RESTListener m_restListener;
    private TokenVerifier m_tokenVerifier;

    public WotCoapResource(String name, RESTListener restListener, TokenVerifier tokenVerifier) {
        super(name);
        this.m_restListener = restListener;
        this.m_tokenVerifier = tokenVerifier;
    }

    //TODO set this right
    private static int TOKEN_BEARER_OPTION_NUMBER = 42;

    private boolean authorize(CoapExchange exchange) {
        String jwt = null;
        List<Option> options = exchange.getRequestOptions().asSortedList();
        int idx = Collections.binarySearch(options, new Option(TOKEN_BEARER_OPTION_NUMBER));

        if (idx >= 0) {
            jwt = options.get(idx).getStringValue();
        } else {
            //no token, jwt == null
        }

        return m_tokenVerifier.isAuthorized(jwt);
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        if (!authorize(exchange)) {
            exchange.respond(CoAP.ResponseCode.UNAUTHORIZED);
            return;
        }
        try {
            byte[] response = m_restListener.onGet();
            exchange.respond(CoAP.ResponseCode.CONTENT, response, TEXT_PLAIN);
        } catch (UnsupportedOperationException e) {
            exchange.respond(CoAP.ResponseCode.METHOD_NOT_ALLOWED);
        }
    }

    @Override
    public void handlePUT(CoapExchange exchange) {
        if (!authorize(exchange)) {
            exchange.respond(CoAP.ResponseCode.UNAUTHORIZED);
            return;
        }
        try {
            m_restListener.onPut(exchange.getRequestText().getBytes());
            exchange.respond(CoAP.ResponseCode.CHANGED);
        } catch (UnsupportedOperationException e) {
            exchange.respond(CoAP.ResponseCode.METHOD_NOT_ALLOWED);
        } catch (IllegalArgumentException e) {
            exchange.respond(CoAP.ResponseCode.BAD_REQUEST);
        }

    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        if (!authorize(exchange)) {
            exchange.respond(CoAP.ResponseCode.UNAUTHORIZED);
            return;
        }
        try {
            byte[] resp = m_restListener.onPost(exchange.getRequestText().getBytes());
            //TODO: add Location Option to response
            exchange.respond(CoAP.ResponseCode.CREATED, resp, TEXT_PLAIN);
        } catch (UnsupportedOperationException e) {
            exchange.respond(CoAP.ResponseCode.METHOD_NOT_ALLOWED);
        } catch (IllegalArgumentException e) {
            exchange.respond(CoAP.ResponseCode.BAD_REQUEST);
        }

    }

    @Override
    public void handleDELETE(CoapExchange exchange) {
        if (!authorize(exchange)) {
            exchange.respond(CoAP.ResponseCode.UNAUTHORIZED);
            return;
        }
        try {
            m_restListener.onDelete();
            exchange.respond(CoAP.ResponseCode.DELETED);
        } catch (UnsupportedOperationException e) {
            exchange.respond(CoAP.ResponseCode.METHOD_NOT_ALLOWED);
        }
    }
}
