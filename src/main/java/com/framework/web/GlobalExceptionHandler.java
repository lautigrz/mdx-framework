package com.framework.web;

import com.framework.web.response.HttpResponseWriter;
import com.framework.web.response.ResponseConverter;
import com.framework.web.response.ResponsePayload;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GlobalExceptionHandler {
    private static final Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getName());

    private final Map<Class<? extends Exception>, Integer> statusMap = new HashMap<>();

    private final HttpResponseWriter responseWriter;
    private final ResponseConverter responseConverter;

    public GlobalExceptionHandler(HttpResponseWriter responseWriter, ResponseConverter responseConverter) {
        this.responseWriter = responseWriter;
        this.responseConverter = responseConverter;

    }

    public void registerException(Class<? extends Exception> exceptionClass, int httpStatus) {
        statusMap.put(exceptionClass, httpStatus);
    }

    public void handleException(HttpExchange exchange, Exception e) throws IOException {

        Throwable rootCause = e;
        if (e instanceof InvocationTargetException) {
            rootCause = ((InvocationTargetException) e).getTargetException();
        }

        int statusCode = statusMap.getOrDefault(rootCause.getClass(), 500);

        if (statusCode == 500) {
            logger.log(Level.SEVERE, "Error crítico no manejado", rootCause);
        } else {
            logger.log(Level.INFO, "Error controlado ({0}): {1}", new Object[]{statusCode, rootCause.getMessage()});
        }
        String message = rootCause.getMessage();
        if (message == null) message = "Error interno del servidor";

        ResponsePayload payload = responseConverter.convertError(statusCode, message);
        responseWriter.writeResponse(exchange, statusCode, payload);
    }
}
