package de.webthing.binding.coap;

import de.webthing.binding.Binding;
import de.webthing.binding.RESTListener;
import de.webthing.binding.ResourceBuilder;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.server.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CoapBinding implements Binding {

    private static final Logger log = LoggerFactory.getLogger(CoapBinding.class);
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
                if(parts.length == 0) return;

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

                String lastPart = parts[parts.length - 1];
                // TODO this has a side-effect: replacing the resource makes the children unreachable
                // a clean tree-replace would require to store the children and append them to the new resource
                current.add(new WotCoapResource(lastPart, restListener));
            }
		};
	}

	@Override
	public void start() {
		m_coapServer.start();
	}

}
