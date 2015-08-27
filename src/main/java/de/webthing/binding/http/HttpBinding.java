package de.webthing.binding.http;

import java.io.IOException;

import de.webthing.binding.RESTListener;
import de.webthing.binding.ResourceBuilder;


public class HttpBinding {
	
	public void initialize() throws IOException {
		m_server = new NanoHttpServer();
	}
	
	
	public ResourceBuilder getResourceBuilder() {
		return new ResourceBuilder() {
			@Override
			public void newResource(String url, RESTListener restListener) {
			}
		};
	}

	
	public void start() throws IOException {
		m_server.start();
	}
	
	
	private NanoHttpServer m_server;
}
