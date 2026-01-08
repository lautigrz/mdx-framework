package com.framework.web.response;

public interface ResponseConverter {
    ResponsePayload convert(Object result);
    ResponsePayload convertError(int code, String messageError);
}
