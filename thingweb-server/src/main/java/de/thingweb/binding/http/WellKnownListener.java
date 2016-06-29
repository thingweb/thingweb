package de.thingweb.binding.http;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.thingweb.binding.AbstractRESTListener;
import de.thingweb.thing.Content;
import de.thingweb.thing.HyperMediaLink;
import de.thingweb.util.encoding.ContentHelper;
import javafx.util.Pair;

public class WellKnownListener extends AbstractRESTListener {
	
	public static final String WELL_KNOWN_URL = "/.well-known/wot";
	private List<HyperMediaLink> links = new ArrayList<>();
	
	@Override
	public Content onGet() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writer().writeValueAsString(links);
		return ContentHelper.makeStringValue(json);
	}
	
	@Override
	public List<Pair<String, String>> getHeaders() {
		if(links.size() == 0)
			return null;
		ArrayList<Pair<String, String>> headers = new ArrayList<>();
		String value = "";
		for(HyperMediaLink link : links)
			value += "<" + link.getHref() + ">;rel=thing ";
		headers.add(new Pair<String,String>("Link", value));
		return headers;
	}
	
	public void addLink(HyperMediaLink link){
		links.add(link);
	}

}
