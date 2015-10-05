package de.webthing.binding.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import de.webthing.binding.GrantAllTokenVerifier;
import de.webthing.binding.RESTListener;
import de.webthing.binding.ResourceBuilder;
import de.webthing.binding.TokenVerifier;
import fi.iki.elonen.NanoHTTPD;


public class NanoHttpServer extends NanoHTTPD  implements ResourceBuilder {

    private final Map<String,RESTListener> resmap = new HashMap<>();
    private final TokenVerifier tokenVerifier = new GrantAllTokenVerifier();


	public NanoHttpServer() throws IOException {
        super(8080);
    }

    @Override
    public Response serve(IHTTPSession session) {
         String uri = session.getUri();

        //compare uri against resmap
        RESTListener listener = resmap.get(uri);

        //FIXME partial uris and overviews should be added
        
        //if not found return 404
        if(listener== null) {
            return new Response(Response.Status.NOT_FOUND,MIME_PLAINTEXT,"Resource not found");
        }

        byte[] result = null;

        if(!authorize(session)) {
            return new Response(Response.Status.UNAUTHORIZED,MIME_PLAINTEXT,"Unauthorízed");
        }


        //get result
        switch (session.getMethod()) {
            case GET:
                 result = listener.onGet();
                return new Response(new String(result));
            case PUT:
                listener.onPut(getPayload(session).getBytes());
                return new Response(null);
            case POST:
                result = listener.onPost(getPayload(session).getBytes());
                return new Response(new String(result));
            case DELETE:
                listener.onDelete();
                return new Response(null);
            default:
                return new Response(Response.Status.METHOD_NOT_ALLOWED,MIME_PLAINTEXT,"Method not allowed");
        }

	}

    private boolean authorize(IHTTPSession session) {
        String jwt = null;
        String auth = session.getHeaders().get("Authorization");
        if(auth != null) {
            if(auth.startsWith("Bearer ")) {
                jwt = auth.substring("Bearer ".length());
            }
        }
        return tokenVerifier.isAuthorized(jwt);
    }

    private static String getPayload(IHTTPSession session) {
        return convertStreamToString(session.getInputStream());
    }

    //Stackoverflow question 309424
    private static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @Override
    public void newResource(String url, RESTListener restListener) {
        resmap.put(url,restListener);
    }
}
