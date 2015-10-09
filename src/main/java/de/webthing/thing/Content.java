package de.webthing.thing;

public class Content {
	
	final byte[] content;
	final MediaType mediaType;
	
	public Content(byte[] content, MediaType mediaType) {
		this.content = content;
		this.mediaType = mediaType;
	}
	
	public MediaType getMediaType() {
		return this.mediaType;
	}
	
	public byte[] getContent() {
		return this.content;
	}
	
}
