package com.framework.web;
import com.framework.context.ApplicationContext;
import com.framework.enums.HttpMethod;
import com.framework.exception.BadRequestException;
import com.framework.exception.RouteNotFoundException;
import com.framework.util.HttpRequestParser;
import com.framework.web.response.HttpResponseWriter;
import com.framework.web.response.ResponseConverter;
import com.framework.web.response.ResponsePayload;
import com.framework.web.routing.RouteMatch;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DispatcherHandler implements HttpHandler {

    private static final Logger logger = Logger.getLogger(DispatcherHandler.class.getName());

    private final RouteRegistry routeRegistry;
    private final HandlerAdapter handlerAdapter;
    private final HttpResponseWriter responseWriter;
    private final ResponseConverter responseConverter;
    private final GlobalExceptionHandler globalExceptionHandler;

    public DispatcherHandler(ApplicationContext context,
                             HttpResponseWriter responseWriter,
                             ResponseConverter responseConverter) {
        this.handlerAdapter = new HandlerAdapter();
        this.routeRegistry = new RouteRegistry(context);
        this.responseWriter = responseWriter;
        this.responseConverter = responseConverter;

        this.globalExceptionHandler = new GlobalExceptionHandler(responseWriter, responseConverter);
        this.globalExceptionHandler.registerException(RouteNotFoundException.class, 404);
        this.globalExceptionHandler.registerException(BadRequestException.class, 400);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            HttpMethod method = HttpMethod.valueOf(exchange.getRequestMethod());

            logger.log(Level.INFO, "Received " + method + " request for " + path);

            RouteMatch match = routeRegistry.getRoute(method, path);

            validateRoute(match, path);

            RouteEntry entry = match.routeEntry;

            WebRequest webRequest = buildWebRequest(exchange, match, path);

            Object result = handlerAdapter.execute(entry, webRequest);

            ResponsePayload payload = responseConverter.convert(result);

            responseWriter.writeResponse(exchange, 200, payload);

        } catch (Exception e) {
            handleException(exchange,e);
        }

    }

    private WebRequest buildWebRequest(HttpExchange exchange, RouteMatch match, String path) throws IOException {
        String body = HttpRequestParser.parseBody(exchange);
        Map<String, String> pathParams = match.pathVariables;
        Map<String, String> queryParams = HttpRequestParser.parseQueryParameters(exchange.getRequestURI().getQuery());
        return new WebRequest(queryParams,pathParams,  body);

    }

    private void handleException(HttpExchange exchange, Exception e) throws IOException {

         this.globalExceptionHandler.handleException(exchange, e);
    }

    private void validateRoute(RouteMatch match, String path) {
        if (match == null) {
            throw new RouteNotFoundException("No se encontró ruta para: " + path);
        }

    }

}
