package de.webthing.binding.http;

import de.webthing.binding.Binding;
import de.webthing.binding.ResourceBuilder;

import java.io.IOException;


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
