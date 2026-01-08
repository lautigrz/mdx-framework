package com.framework.exception;

public class ResponseConversionException extends RuntimeException {
    public ResponseConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
