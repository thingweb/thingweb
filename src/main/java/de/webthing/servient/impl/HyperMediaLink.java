package de.webthing.servient.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Created by mchn1210 on 20.10.2015.
 */

@JsonTypeName("Link")
public class HyperMediaLink {

    @JsonProperty("rel")
    private String rel;

    @JsonProperty("href")
    private String href;

    @JsonProperty("method")
    private String method = "GET";

    public HyperMediaLink(String rel, String href) {
        this.rel = rel;
        this.href = href;
    }
}
