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

package de.thingweb.binding.http;

import de.thingweb.binding.RESTListener;
import de.thingweb.binding.ResourceBuilder;
import de.thingweb.security.TokenExpiredException;
import de.thingweb.security.UnauthorizedException;
import de.thingweb.thing.Content;
import de.thingweb.thing.HyperMediaLink;
import de.thingweb.thing.MediaType;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import javafx.util.Pair;

import org.jose4j.json.internal.json_simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;


public class NanoHttpServer extends NanoHTTPD  implements ResourceBuilder {

	public static final int DEFAULT_PORT = 8080;
	private final Map<String,RESTListener> resmap = new LinkedHashMap<>();
	private Logger log = LoggerFactory.getLogger(NanoHttpServer.class);
	private final String baseuri;
	private WellKnownListener wellKnownListener = new WellKnownListener();

	public NanoHttpServer() throws IOException {
        this(DEFAULT_PORT);
    }

	public NanoHttpServer(int port) throws IOException {
		super(port);
		String hostname = InetAddress.getLocalHost().getHostName();
		baseuri = String.format("http://%s:%s",hostname, port);
		resmap.put(WellKnownListener.WELL_KNOWN_URL, wellKnownListener);
	}

    @Override
    public Response serve(IHTTPSession session) {
         String uri = session.getUri();

        //compare uri against resmap
        RESTListener listener = resmap.get(uri.toLowerCase());

        //if not found return 404
        if(listener== null) {
            String msg = String.format("Resource %s not found, availiable resources:\n",uri);
			msg += resmap.keySet().stream().collect(Collectors.joining("\n"));

			return new Response(Response.Status.NOT_FOUND,MIME_PLAINTEXT,msg);
        }        

		//validate token
		if(listener.hasProtection()) {
			try {
				String jwt = null;
				String auth = session.getHeaders().get("authorization");
				if (auth != null) {
					if (auth.startsWith("Bearer ")) {
						jwt = auth.substring("Bearer ".length());
					}
				} else {
					return new Response(Response.Status.UNAUTHORIZED, MIME_PLAINTEXT, "Empty or not present authorization header");
				}
				listener.validate(session.getMethod().name(), uri, jwt);
			} catch (TokenExpiredException e) {
				return new Response(Response.Status.UNAUTHORIZED, MIME_PLAINTEXT, "Your token has expired");
			} catch (UnauthorizedException e) {
				return new Response(Response.Status.UNAUTHORIZED, MIME_PLAINTEXT, "Unauthorized: " + e.getMessage());
			}
		}

		//get result
        try {
			Response res;
			switch (session.getMethod()) {
			    case GET:
			    	Content resp = listener.onGet();
			    	// TODO how to handle accepted mimeTypes
			    	// e.g., accept=text/html,application/xhtml+xml,application/xml;
			    	String mediaType = resp.getMediaType().mediaType;
			    	if(resp.getMediaTypeEx() != null)
			    		mediaType = resp.getMediaTypeEx();
			    	
			    	res = new Response(Status.OK, mediaType,  new ByteArrayInputStream(resp.getContent()));
			    	if(listener.getHeaders() != null){
			    		for(Pair<String,String> header : listener.getHeaders()){
			    			res.addHeader(header.getKey(), header.getValue());
			    		}
			    	}
					break;
			    case PUT:
			        listener.onPut(getPayload(session));
			        res = new Response(null);
					break;
			    case POST:
			    	resp = listener.onPost(getPayload(session));
			    	res = new Response(Status.OK, resp.getMediaType().mediaType, new String(resp.getContent()));
					break;
			    case DELETE:
			        listener.onDelete();
			        res = new Response(null);
					break;
				case OPTIONS:
					res = new Response(null);
					break;
			    default:
			        res = new Response(Response.Status.METHOD_NOT_ALLOWED,MIME_PLAINTEXT,"Method not allowed");
			}
			return addCORSHeaders(res);
		} catch (UnsupportedOperationException e) {
			return addCORSHeaders(new Response(Status.METHOD_NOT_ALLOWED, MIME_PLAINTEXT, e.toString()));
		} catch (IllegalArgumentException e) {
			return addCORSHeaders(new Response(Status.BAD_REQUEST, MIME_PLAINTEXT, e.toString()));
		} catch (Exception e) {
			log.error("callback raised error", e);
        	JSONObject errorObject = new JSONObject();
        	errorObject.put("errorMessage", e.getMessage());
        	String responsePayload = errorObject.toJSONString();
			return addCORSHeaders(new Response(Response.Status.INTERNAL_ERROR, "application/json", responsePayload));
		}

	}

	private Response addCORSHeaders(Response resp) {
		// TODO defaulting cors - should be configurable
		final String DEFAULT_ALLOWED_HEADERS = "origin,accept,content-type";
		final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";

		resp.addHeader("Access-Control-Allow-Origin","*");
		resp.addHeader("Access-Control-Allow-Headers", DEFAULT_ALLOWED_HEADERS);
		resp.addHeader("Access-Control-Allow-Credentials", "true");
		resp.addHeader("Access-Control-Allow-Methods", ALLOWED_METHODS);
		//resp.addHeader("Access-Control-Max-Age", "" + MAX_AGE);

		return resp;
	}

    private static Content getPayload(IHTTPSession session) throws IOException {
    	// Daniel: to get rid of socket timeout 
    	// http://stackoverflow.com/questions/22349772/retrieve-http-body-in-nanohttpd
    	Integer contentLength = Integer.parseInt(session.getHeaders().get("content-length"));
    	byte[] buffer = new byte[contentLength];
    	int len = 0;
    	do {
    		len += session.getInputStream().read(buffer, len, (contentLength-len));
    	} while(len < contentLength);
    	
    	String contentType = session.getHeaders().get("content-type"); // e.g., content-type=text/plain; charset=UTF-8
    	MediaType mt = MediaType.UNDEFINED; // unknown type
    	if(!(contentType == null || contentType.length() == 0)) {
    		StringTokenizer st = new StringTokenizer(contentType, "; \n\r");
    		String t = st.nextToken();
    		mt = MediaType.getMediaType(t); // may throw exception if content-type is unknown
    	}
    	Content c = new Content(buffer, mt);
    	return c;
    }

    @Override
    public void newResource(String url, RESTListener restListener) {
        resmap.put("/" + url.toLowerCase(),restListener);
        if(url.endsWith(".td"))
        	wellKnownListener.addLink(new HyperMediaLink("thing", url));
    }

	@Override
	public String getBase() {
		return baseuri;
	}

	@Override
	public String getIdentifier() {
		return "HTTP";
	}

	@Override
	public void removeResource(String url) {
		// TODO Auto-generated method stub
		
	}
}
