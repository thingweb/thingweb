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

/**
 * Created by Sebastian Kaebisch on 10.12.2015.
 */

// UNIRES, Jetty

package de.thingweb.discovery;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/** Class to interact with a TD repository
 * <p>https://github.com/thingweb/thingweb-repository</p>
 *  */
public class TDRepository {
	
	public static final String ETH_URI = "vs0.inf.ethz.ch:8080";

	private String repository_uri;

	/** Constructer set up the endpoint address of the TD repository
	 * @param repository_uri Repository Uri (+Port if needed)
	 */
	public  TDRepository(String repository_uri) {
		this.repository_uri = repository_uri;
	}
	
	/**  This method takes a SPARQL query and send it o the TD repository    
	 * @param search SPARQL query
	 * @return JSONObject array of relevant TD files (=empty array means no match)
	 * @throws Exception error
	 * */
	public JSONObject tdTripleSearch(String search) throws Exception  {
		
		// if triple search contains spaces, replaces with  %20
		//String search_without_space = search.replace(" ", " %20");
		search = URLEncoder.encode(search, "UTF-8");
		
		URL myURL = new URL("http://" + repository_uri + "/td?query=" + search);
		HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
		myURLConnection.setRequestMethod("GET");
		myURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		myURLConnection.setDoInput(true);
		myURLConnection.setDoOutput(true);

		InputStream in = myURLConnection.getInputStream();
								
	//	JSONArray jsonLDs = new JSONArray(streamToString(in));
		
		JSONObject jsonLDs = new JSONObject(streamToString(in));

		System.out.println(jsonLDs);
		
		return jsonLDs;
	}
	
	/** This method takes a free text search and send it o the TD repository 
	 * @param search free text search
	 * @return JSONObject of relevant TD files (=empty array means no match)
	 * @throws Exception error
	 * */
	public JSONObject tdFreeTextSearch(String search) throws Exception  {
		
		search = URLEncoder.encode(search, "UTF-8");
		
		URL myURL = new URL("http://" + repository_uri + "/td?query=" + search);
		HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
		myURLConnection.setRequestMethod("GET");
		myURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		myURLConnection.setDoInput(true);
		myURLConnection.setDoOutput(true);

		InputStream in = myURLConnection.getInputStream();
								
	//	JSONArray jsonLDs = new JSONArray(streamToString(in));
		
		JSONObject jsonLDs = new JSONObject(streamToString(in));

		System.out.println(jsonLDs);
		
		return jsonLDs;
	}
	
	/** This method request the TD repository to return the names of all known Things  
	 * @return JSONObject array of Things names (=empty array means no Thing is present in TD repository)
	 * @throws Exception error
	 * */
	public JSONObject nameOfThings() throws Exception  {
		return tdTripleSearch("?s ?p ?o");
		
//		//String search = "search = URLEncoder.encode(search, "UTF-8");
//		String search = "{ ?td <http://www.w3c.org/wot/td#hasMetadata> ?m . ?m <http://www.w3c.org/wot/td#name> \"query text\" . }";
//
//		URL myURL = new URL("http://"+repository_uri+":"+repository_port+"/td?query="+search);
//		HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
//		myURLConnection.setRequestMethod("GET");
//		myURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//		myURLConnection.setDoInput(true);
//		myURLConnection.setDoOutput(true);
//
//		InputStream in = myURLConnection.getInputStream();
//								
//	//	JSONArray jsonLDs = new JSONArray(streamToString(in));
//		
//		JSONObject jsonLDs = new JSONObject(streamToString(in));
//
//		System.out.println(jsonLDs);
//		
//		return jsonLDs;
//		";
//		URL myURL = new URL("http://"+repository_uri+":"+repository_port+"/td?query="+search);
//		HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
//		myURLConnection.setRequestMethod("GET");
//		myURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//		myURLConnection.setDoInput(true);
//		myURLConnection.setDoOutput(true);
//
//		InputStream in = myURLConnection.getInputStream();
// 
//		JSONObject jsonLDs = new JSONObject(streamToString(in));
//
//		System.out.println(jsonLDs);
//		
//		return jsonLDs;
 
	}
	
	/**
	 * Adds a ThingDescription to Repository
	 * 
	 * @param content JSON-LD
	 * @return key of entry in repository
	 * @throws Exception in case of error
	 */
	public String addTD(byte[] content) throws Exception {
		URL url = new URL("http://" + repository_uri  + "/td");
		HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
		httpCon.setDoOutput(true);
		httpCon.setRequestProperty("content-type", "application/ld+json");
		httpCon.setRequestMethod("POST");
		OutputStream out = httpCon.getOutputStream();
		out.write(content);
		out.close();
		
		InputStream is = httpCon.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int b;
		while ((b = is.read()) != -1) {
			baos.write(b);
		}
		
		int responseCode = httpCon.getResponseCode();
		
		httpCon.disconnect();
		
		String key = ""; // new String(baos.toByteArray());
		
		if (responseCode != 201) {
			// error
			throw new RuntimeException("ResponseCodeError: " + responseCode);
		} else {
			Map<String, List<String>> hf = httpCon.getHeaderFields();
			List<String> los = hf.get("Location");
			if(los != null && los.size() > 0) {
				key = los.get(0);
			}
		}
		
		return key;
	}
	
	/**
	 * Update existing TD
	 * 
	 * @param key in repository (/td/{id})
	 * @param content JSON-LD
	 * @throws Exception in case of error
	 */
	public void updateTD(String key, byte[] content) throws Exception {
		URL url = new URL("http://" + repository_uri  + key);
		HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
		httpCon.setDoOutput(true);
		httpCon.setRequestProperty("content-type", "application/ld+json");
		httpCon.setRequestMethod("PUT");
		OutputStream out = httpCon.getOutputStream();
		out.write(content);
		out.close();
		
		
//		InputStream is = httpCon.getInputStream();
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		int b;
//		while ((b = is.read()) != -1) {
//			baos.write(b);
//		}
		
		int responseCode = httpCon.getResponseCode();

		httpCon.disconnect();
		
		if (responseCode != 200) {
			// error
			throw new RuntimeException("ResponseCodeError: " + responseCode);
		}
	}
	
	
	/**
	 * Deletes repository by using key
	 * 
	 * @param key in repository (/td/{id})
	 * @throws Exception in case of error
	 */
	public void deleteTD(String key) throws Exception {
		URL url = new URL("http://" + repository_uri + key);
		HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
		httpCon.setDoOutput(true);
		httpCon.setRequestProperty("content-type", "application/ld+json");
		httpCon.setRequestMethod("DELETE");
		// httpCon.connect();
		int responseCode = httpCon.getResponseCode();
		if (responseCode != 200) {
			// error
			throw new RuntimeException("ResponseCodeError: " + responseCode);
		}
	}
	
	
	
	
	
	/** Brings input stream into string representation  */
	private  String streamToString(InputStream in) throws IOException {
		  StringBuilder out = new StringBuilder();
		  BufferedReader br = new BufferedReader(new InputStreamReader(in));
		  for(String line = br.readLine(); line != null; line = br.readLine()) 
		    out.append(line);
		  br.close();
		  return out.toString();
		}
}
