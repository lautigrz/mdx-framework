package com.framework.web.response;

import com.framework.enums.ContentType;

public final class ResponsePayload {

    private final byte[] body;
    private final ContentType contentType;

    public ResponsePayload(byte[] data, ContentType contentType) {
        this.body = data;
        this.contentType = contentType;
    }

    public byte[] body() {
        return body;
    }
    public ContentType contentType() {
        return contentType;
    }

}
