package com.framework.enums;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public enum ContentType {
    TEXT_PLAIN("text/plain"),
    APPLICATION_JSON("application/json");

    private final String mediaType;
    private final Charset charset;

    ContentType(String mediaType) {
        this(mediaType, StandardCharsets.UTF_8);
    }

    ContentType(String mediaType, Charset charset) {
        this.mediaType = mediaType;
        this.charset = charset;
    }

    public String value() {
        return mediaType + "; charset=" + charset.name();
    }

    public String mediaType() {
        return mediaType;
    }

    public Charset charset() {
        return charset;
    }
}
