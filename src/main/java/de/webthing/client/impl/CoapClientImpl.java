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

import de.webthing.client.Callback;
import de.webthing.desc.pojo.Protocol;

public class CoapClientImpl extends AbstractClientImpl {
	
	private static final Logger log = LoggerFactory.getLogger(CoapClientImpl.class);
	
	Map<String, CoapObserveRelation> observes = new HashMap<>();
	
	/** e.g., http://www.example.com:80/ledlamp */
	final String coapRoot;
	final String coapProperties = "/properties/";
	final String coapActions = "/actions/";
	
	public CoapClientImpl() {
		super();
		
		// TODO use the ones there.. currently not usable
		// see mismatching coap://www.example.com:5683/ledlamp
		@SuppressWarnings("unused")
		List<Protocol> prots = getProtocols();
		log.warn("TODO use thing description protocols (currently 'coap://localhost:5683/thingsMyLED' is used)");

		// TODO use clientListener information
		coapRoot = "coap://localhost:5683/thingsMyLED";
	}
	
	

//	String sc = coapRoot + "properties/colorTemperature";
//	String sc = "coap://localhost:5683/thingsMyLED";
//	CoapClient coap = new CoapClient(sc);
//
//	// synchronous
//	String content1 = coap.get().getResponseText();
//	System.out.println("bla: " + content1);
	
	
	public void put(String propertyName, String propertyValue, Callback callback) {
		CoapClient coap = new CoapClient(coapRoot + coapProperties + propertyName);
		// TODO retrieve format client listener
		coap.put(new CoapHandler() {

			@Override
			public void onLoad(CoapResponse response) {
				callback.onPut(propertyName, response.getResponseText());
			}

			@Override
			public void onError() {
				callback.onPutError(propertyName);
			}
		}, propertyValue, org.eclipse.californium.core.coap.MediaTypeRegistry.APPLICATION_JSON);
	}
	
	public void get(String propertyName, Callback callback) {
		CoapClient coap = new CoapClient(coapRoot + coapProperties + propertyName);

		// asynchronous
		coap.get(new CoapHandler() {
			@Override
			public void onLoad(CoapResponse response) {
				String content = response.getResponseText();
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
				callback.onObserve(propertyName, response.getResponseText());
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
	
	public void action(String actionName, String actionValue, Callback callback) {
		// TODO similar to PUT ?
		
		CoapClient coap = new CoapClient(coapRoot + coapActions + actionName);
		coap.put(new CoapHandler() {

			@Override
			public void onLoad(CoapResponse response) {
				callback.onAction(actionName, response.getResponseText());
			}

			@Override
			public void onError() {
				callback.onActionError(actionName);
			}
		}, actionValue, org.eclipse.californium.core.coap.MediaTypeRegistry.APPLICATION_JSON);
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
