package de.webthing.binding.http;

import java.io.IOException;

import de.webthing.binding.Binding;
import de.webthing.binding.RESTListener;
import de.webthing.binding.ResourceBuilder;


public class HttpBinding implements Binding {

	@Override
	public void initialize() throws IOException {
			m_server = new NanoHttpServer();
	}
	
	@Override
	public ResourceBuilder getResourceBuilder() {
		return m_server;
	}

	@Override
	public void start() throws IOException {
		m_server.start();
	}
	
	
	private NanoHttpServer m_server;
}
