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

package de.webthing.util.encoding;

import de.webthing.thing.Content;
import de.webthing.thing.MediaType;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class JSONContent {
	
	public static final String JSON_ROOT = "JSON";
	private static final String JSON_ROOT_WRAPPER_PREFIX = "{\"" + JSON_ROOT + "\": ";
	private static final String JSON_ROOT_WRAPPER_POSTFIX = "}";
	
	public static JSONObject parseJSON(Content content) throws UnsupportedEncodingException {
		if(!(content.getMediaType() != MediaType.APPLICATION_JSON || content.getMediaType() != MediaType.TEXT_PLAIN || content.getMediaType() != MediaType.UNDEFINED)) {
			// throw error
			throw new UnsupportedEncodingException("Unsupported mediaType: " + content.getMediaType());
		}
		
		String s = JSON_ROOT_WRAPPER_PREFIX + new String(content.getContent()) + JSON_ROOT_WRAPPER_POSTFIX;
		
		JSONObject obj = new JSONObject(s);
		System.out.println(obj);
		return obj;
	}
	
	public static Content getContent(String jsonText) {
		Content content = new Content(jsonText.getBytes(), MediaType.APPLICATION_JSON);
		return content;
	}
	
	public static Content getContent(JSONObject jsonObject) {
		Content content = new Content(jsonObject.toString().getBytes(), MediaType.APPLICATION_JSON);
		return content;
	}

}
