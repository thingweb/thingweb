package de.webthing.util.encoding;

import java.io.UnsupportedEncodingException;

import org.json.JSONObject;

import de.webthing.thing.Content;
import de.webthing.thing.MediaType;

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
