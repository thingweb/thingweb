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

package de.thingweb.binding.coap;

import de.thingweb.binding.Binding;
import de.thingweb.binding.RESTListener;
import de.thingweb.binding.ResourceBuilder;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.server.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;


public class CoapBinding implements Binding {

    private static final Logger log = LoggerFactory.getLogger(CoapBinding.class);
	private CoapServer m_coapServer;
    private final String baseuri;

    public CoapBinding() {
        String hostname = null;
        try {
            hostname = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            hostname = "localhost";
        }
        baseuri = String.format("coap://%s",hostname);;
    }

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
                Resource existing = current.getChild(lastPart);
                WotCoapResource newRes = new WotCoapResource(lastPart, restListener);

                if(existing != null) {
                    Collection<Resource> children = existing.getChildren();
                    children.forEach(newRes::add);
                }

                current.add(newRes);
            }
			
			@Override
            public void removeResource(String url) {
                String[] parts = url.split("/");
                if(parts.length == 0) return;

                Resource current = m_coapServer.getRoot();

                for (int i = 0; i < parts.length - 1; i++) {
                    if (parts[i].isEmpty()) {
                        continue;
                    }
                    Resource child = current.getChild(parts[i]);
                    current = child;
                }

                String lastPart = parts[parts.length - 1];
                Resource existing = current.getChild(lastPart);
                
                while(true){
                	Resource parent = existing.getParent();
                	parent.remove(existing);
                	if(parent.getChildren().size() != 0)
                		break;
                	existing = parent;
                }               
            }			

            @Override
            public String getBase() {
                return baseuri;
            }

            @Override
            public String getIdentifier() {
                return "CoAP";
            }
        };
	}

	@Override
	public void start() {
		m_coapServer.start();
	}

    @Override
    public void stop() throws IOException {
        m_coapServer.stop();
    }

}
