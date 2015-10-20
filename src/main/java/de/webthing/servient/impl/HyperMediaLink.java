package de.webthing.servient.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by mchn1210 on 20.10.2015.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HyperMediaLink {

    @JsonProperty("rel")
    private String rel;

    @JsonProperty("href")
    private String href;

    @JsonProperty("method")
    private String method;

    @JsonProperty("mediaType")
    private String mediaType;

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public HyperMediaLink(String rel, String href) {
        this.rel = rel;
        this.href = href;
    }

    public HyperMediaLink(String rel, String href, String method, String mediaType) {
        this.rel = rel;
        this.href = href;
        this.method = method;
        this.mediaType = mediaType;
    }
}
