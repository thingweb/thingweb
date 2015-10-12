package de.webthing.binding.coap;

import de.webthing.binding.Binding;
import de.webthing.binding.RESTListener;
import de.webthing.binding.ResourceBuilder;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.server.resources.Resource;


public class CoapBinding implements Binding {

	private CoapServer m_coapServer;

	@Override
	public void initialize() {
		m_coapServer = new CoapServer();
	}

	@Override
	public ResourceBuilder getResourceBuilder() {
		return new ResourceBuilder() {
			@Override
            public void newResource(String url, RESTListener restListener) {
                String[] parts = url.split("/");

                Resource current = m_coapServer.getRoot();
                for (int i = 0; i < parts.length - 1; i++) {
                    if (parts[i].isEmpty()) {
                        continue;
                    }

                    Resource child = current.getChild(parts[i]);

                    if (child == null) {
                        child = new CoapResource(parts[i]);
                        current.add(child);
                    }

                    current = child;
                }

                current.add(new WotCoapResource(parts[parts.length - 1], restListener));
            }
		};
	}

	@Override
	public void start() {
		m_coapServer.start();
	}

}
