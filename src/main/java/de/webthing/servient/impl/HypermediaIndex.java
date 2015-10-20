package de.webthing.servient.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import de.webthing.binding.RESTListener;
import de.webthing.thing.Content;
import de.webthing.thing.MediaType;

import java.util.Arrays;
import java.util.List;

/**
 * Created by mchn1210 on 20.10.2015.
 */
public class HypermediaIndex implements RESTListener {

    private static final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    private static final ObjectMapper mapper = new ObjectMapper();

    private Content myContent;

    public HypermediaIndex(List<HyperMediaLink> links) {
        myContent = createContent(links);
    }

    public HypermediaIndex(HyperMediaLink... link) {
        myContent = createContent(Arrays.asList(link));
    }

    private Content createContent(List<HyperMediaLink> links) {
        String json = null;
        try {
            json = ow.writeValueAsString(links);
        } catch (JsonProcessingException e) {
            json = "{ \"error\" : \" " + e.getMessage() + "\"  }";
        }
        return new Content(json.getBytes(),MediaType.APPLICATION_JSON);
    }

    @Override
    public Content onGet() throws UnsupportedOperationException, RuntimeException {
        return myContent;
    }

    @Override
    public void onPut(Content data) throws UnsupportedOperationException, IllegalArgumentException, RuntimeException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Content onPost(Content data) throws UnsupportedOperationException, IllegalArgumentException, RuntimeException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void onDelete() throws UnsupportedOperationException, RuntimeException {
        throw new UnsupportedOperationException("not implemented");
    }
}
