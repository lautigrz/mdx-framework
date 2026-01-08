package com.framework.web.response;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;


public final class HttpResponseWriter {

    public HttpResponseWriter(){
    }
    public void writeResponse(HttpExchange httpExchange, int code, ResponsePayload responsePayload) throws IOException {

        httpExchange.getResponseHeaders().add("Content-Type", responsePayload.contentType().value());

        httpExchange.sendResponseHeaders(code, responsePayload.body().length);

        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(responsePayload.body());
        }
    }

}
