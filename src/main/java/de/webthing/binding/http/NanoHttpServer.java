package de.webthing.binding.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import de.webthing.binding.RESTListener;
import de.webthing.binding.ResourceBuilder;
import fi.iki.elonen.NanoHTTPD;


public class NanoHttpServer extends NanoHTTPD  implements ResourceBuilder {

    private final Map<String,RESTListener> resmap = new HashMap<>();

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

        String result = null;
        
        //get result
        switch (session.getMethod()) {
            case GET:
                 result = listener.onGet();
                return new Response(result);
            case PUT:
                listener.onPut(getPayload(session));
                return new Response(result);
            case POST:
                result = listener.onPost(getPayload(session));
                return new Response(result);
            case DELETE:
                listener.onDelete();
                return new Response(result);
            default:
                return new Response(Response.Status.METHOD_NOT_ALLOWED,MIME_PLAINTEXT,"Method not allowed");
        }

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
