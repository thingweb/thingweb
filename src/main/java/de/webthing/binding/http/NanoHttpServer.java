package de.webthing.binding.http;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;


public class NanoHttpServer extends NanoHTTPD {
	
	public NanoHttpServer() throws IOException {
        super(8080);
    }
	

    @Override
    public Response serve(IHTTPSession session) {
        return new Response("Hallo");
	}
}
