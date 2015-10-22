/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Siemens AG and the thingweb community
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package de.webthing.client.impl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.californium.core.CoapObserveRelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.webthing.client.Callback;
import de.webthing.client.UnsupportedException;
import de.webthing.desc.pojo.ActionDescription;
import de.webthing.desc.pojo.EventDescription;
import de.webthing.desc.pojo.PropertyDescription;
import de.webthing.desc.pojo.Protocol;
import de.webthing.thing.Content;
import de.webthing.thing.MediaType;

public class HttpClientImpl extends AbstractClientImpl {

	private static final Logger log = LoggerFactory.getLogger(HttpClientImpl.class);

	Map<String, CoapObserveRelation> observes = new HashMap<>();

	final String httpProperties = "/properties/";
	final String httpActions = "/actions/";

	private final int NTHREDS = 10;
	private final ExecutorService executorService = Executors.newFixedThreadPool(NTHREDS);

	public HttpClientImpl(Protocol prot, List<PropertyDescription> properties, List<ActionDescription> actions,
			List<EventDescription> events) {
		super(prot.getUri(), properties, actions, events);
	}

	public void put(String propertyName, Content propertyValue, Callback callback) throws UnsupportedException {
		try {
			CallbackPutTask cgt = new CallbackPutTask(propertyName, propertyValue, callback);
			executorService.submit(cgt);
		} catch (Exception e) {
			log.warn(e.getMessage());
			callback.onGetError(propertyName);
		}
	}

	public void get(String propertyName, Callback callback) throws UnsupportedException {
		try {
			CallbackGetTask cgt = new CallbackGetTask(propertyName, callback);
			executorService.submit(cgt);
		} catch (Exception e) {
			log.warn(e.getMessage());
			callback.onGetError(propertyName);
		}
	}

	public void observe(String propertyName, Callback callback) throws UnsupportedException {
		callback.onObserveError(propertyName);
		// throw new UnsupportedException("Not implemented yet");
	}

	public void observeRelease(String propertyName) throws UnsupportedException {
		throw new UnsupportedException("Not implemented yet");
	}

	public void action(String actionName, Content actionValue, Callback callback) throws UnsupportedException {
		callback.onActionError(actionName);
		// throw new UnsupportedException("Not implemented yet");
	}

	class CallbackGetTask implements Runnable {
		private final String propertyName;
		private final Callback callback;

		CallbackGetTask(String propertyName, Callback callback) {
			this.propertyName = propertyName;
			this.callback = callback;
		}

		public void run() {
			try {
				URL url = new URL(uri + httpProperties + propertyName);
				HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
				httpCon.setRequestMethod("GET");

				InputStream is = httpCon.getInputStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int b;
				while ((b = is.read()) != -1) {
					baos.write(b);
				}

				String contentType = httpCon.getHeaderField("content-type");
				MediaType mediaType = MediaType.getMediaType(contentType);
				
				httpCon.disconnect();
				
				Content c = new Content(baos.toByteArray(), mediaType);

				callback.onGet(propertyName, c);
			} catch (Exception e) {
				log.warn(e.getMessage());
				callback.onGetError(propertyName);
			}
		}
	}

	class CallbackPutTask implements Runnable {
		private final String propertyName;
		private final Callback callback;
		private final Content propertyValue;

		CallbackPutTask(String propertyName, Content propertyValue, Callback callback) {
			this.propertyName = propertyName;
			this.propertyValue = propertyValue;
			this.callback = callback;
		}

		public void run() {
			try {
				URL url = new URL(uri + httpProperties + propertyName);
				HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
				httpCon.setDoOutput(true);
				httpCon.setRequestProperty("content-type", propertyValue.getMediaType().mediaType);
				httpCon.setRequestMethod("PUT");

				OutputStream out = httpCon.getOutputStream();
				out.write(propertyValue.getContent());
				out.close();
				
				InputStream is = httpCon.getInputStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int b;
				while ((b = is.read()) != -1) {
					baos.write(b);
				}

				String contentType = httpCon.getHeaderField("content-type");
				MediaType mediaType = MediaType.getMediaType(contentType);
				
				httpCon.disconnect();
				
				Content c = new Content(baos.toByteArray(), mediaType);

				callback.onPut(propertyName,  c);
			} catch (Exception e) {
				log.warn(e.getMessage());
				callback.onPutError(propertyName);
			}
		}
	}

}
