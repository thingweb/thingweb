package de.webthing.client.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.webthing.binding.coap.WotCoapResource;
import de.webthing.client.Callback;
import de.webthing.desc.pojo.Protocol;
import de.webthing.thing.Content;

public class CoapClientImpl extends AbstractClientImpl {
	
	private static final Logger log = LoggerFactory.getLogger(CoapClientImpl.class);
	
	Map<String, CoapObserveRelation> observes = new HashMap<>();
	
	final String encoding = "JSON";
	
	/** e.g., http://www.example.com:80/ledlamp */
	String coapRoot;
	final String coapProperties = "/properties/";
	final String coapActions = "/actions/";
	
	public CoapClientImpl() {
		super();
	}
	
	

//	String sc = coapRoot + "properties/colorTemperature";
//	String sc = "coap://localhost:5683/thingsMyLED";
//	CoapClient coap = new CoapClient(sc);
//
//	// synchronous
//	String content1 = coap.get().getResponseText();
//	System.out.println("bla: " + content1);
	
	@Override
	protected void processThingDescription() {
		super.processThingDescription();
		
		boolean success = false;
		
		// check for right protocol&encoding
		for(Protocol p: this.protocols) {
			if(p.getUri().startsWith("coap:") && this.encodings.contains(encoding)) {
				// ok, use this one
				log.info("Use Protocol uri='" + p.uri + "' with JSON encoding");
				coapRoot = p.getUri(); // "coap://localhost:5683/thingsMyLED";
				success = true;
				break;
			}
		}
		
		if(!success) {
			// no CoAP/JSON protocol/encoding found
			throw new UnsupportedOperationException("No CoAP/JSON protocol/encoding defined in thing description");
		}
	}
	
	public String getUsedProtocolURI() {
		return this.coapRoot;
	}
	public String getUsedEncoding() {
		return encoding;
	}
	
	
	public void put(String propertyName, Content propertyValue, Callback callback) {
		CoapClient coap = new CoapClient(coapRoot + coapProperties + propertyName);
		coap.put(new CoapHandler() {

			@Override
			public void onLoad(CoapResponse response) {
				Content content = new Content(response.getPayload(), WotCoapResource.getMediaType(response.getOptions()));
				callback.onPut(propertyName, content);
			}

			@Override
			public void onError() {
				callback.onPutError(propertyName);
			}
		}, propertyValue.getContent(), WotCoapResource.getCoapContentFormat(propertyValue.getMediaType()));
	}
	
	public void get(String propertyName, Callback callback) {
		CoapClient coap = new CoapClient(coapRoot + coapProperties + propertyName);

		// asynchronous
		coap.get(new CoapHandler() {
			@Override
			public void onLoad(CoapResponse response) {
				Content content = new Content(response.getPayload(), WotCoapResource.getMediaType(response.getOptions()));
				callback.onGet(propertyName, content);
			}

			@Override
			public void onError() {
				callback.onGetError(propertyName);
			}
		});
	}
	
	
	public void observe(String propertyName, Callback callback) {
		CoapClient coap = new CoapClient(coapRoot + coapProperties + propertyName);
		// observing
		CoapObserveRelation relation = coap.observe(new CoapHandler() {
			@Override
			public void onLoad(CoapResponse response) {
				Content content = new Content(response.getPayload(), WotCoapResource.getMediaType(response.getOptions()));
				callback.onObserve(propertyName, content);
			}

			@Override
			public void onError() {
				callback.onObserveError(propertyName);
			}
		});
		
		observes.put(propertyName, relation);
	}
	
	public void observeRelease(String propertyName) {
		observes.remove(propertyName).proactiveCancel();
	}
	
	public void action(String actionName, Content actionValue, Callback callback) {
		// TODO similar to PUT ?
		
		CoapClient coap = new CoapClient(coapRoot + coapActions + actionName);
		coap.put(new CoapHandler() {

			@Override
			public void onLoad(CoapResponse response) {
				Content content = new Content(response.getPayload(), WotCoapResource.getMediaType(response.getOptions()));
				callback.onAction(actionName, content);
			}

			@Override
			public void onError() {
				callback.onActionError(actionName);
			}
		}, actionValue.getContent(), WotCoapResource.getCoapContentFormat(actionValue.getMediaType()));
	}
	
	
	// simple test
	public static void main(String[] args) throws FileNotFoundException, IOException {

//		// led (local)
//		String jsonld = "jsonld" + File.separator + "led.jsonld";
//		// led (URL)
//		URL jsonld = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/led.jsonld");
		// door
		 URL jsonld = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/door.jsonld");
		
		CoapClientImpl cl = new CoapClientImpl();
		cl.parse(jsonld);
		
	}

}
