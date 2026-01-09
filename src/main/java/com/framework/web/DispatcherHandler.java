package com.framework.web;
import com.framework.context.ApplicationContext;
import com.framework.enums.HttpMethod;
import com.framework.exception.BadRequestException;
import com.framework.exception.RouteNotFoundException;
import com.framework.util.HttpRequestParser;
import com.framework.web.response.HttpResponseWriter;
import com.framework.web.response.ResponseConverter;
import com.framework.web.response.ResponsePayload;
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
    public DispatcherHandler(ApplicationContext context,
                             HttpResponseWriter responseWriter,
                             ResponseConverter responseConverter) {
        this.handlerAdapter = new HandlerAdapter();
        this.routeRegistry = new RouteRegistry(context);
        this.responseWriter = responseWriter;
        this.responseConverter = responseConverter;

    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            logger.log(Level.INFO, "Received " + method + " request for " + path);

            RouteEntry entry = routeRegistry.getRoute(HttpMethod.valueOf(method), path);

            validateRoute(entry, path);

            WebRequest webRequest = buildWebRequest(exchange, entry, path);

            Object result = handlerAdapter.execute(entry, webRequest);

            ResponsePayload payload = responseConverter.convert(result);

            responseWriter.writeResponse(exchange, 200, payload);

        } catch (RouteNotFoundException e) {
            handleException(exchange, 404, e);

        } catch (BadRequestException e) {
            handleException(exchange, 400, e);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error crítico en el servidor", e);
            handleException(exchange, 500, e);
        }

    }

    private WebRequest buildWebRequest(HttpExchange exchange, RouteEntry entry, String path) throws IOException {
        String body = HttpRequestParser.parseBody(exchange);
        Map<String, String> pathParams = HttpRequestParser.parsePathVariables(entry, path);
        Map<String, String> queryParams = HttpRequestParser.parseQueryParameters(exchange.getRequestURI().getQuery());
        return new WebRequest(pathParams, queryParams, body);

    }

    private void handleException(HttpExchange exchange, int statusCode, Exception e) throws IOException {

        ResponsePayload payload = responseConverter.convertError(statusCode, e.getMessage());
        responseWriter.writeResponse(exchange, statusCode, payload);
    }

    private void validateRoute(RouteEntry entry, String path) {
        if (entry == null) {
            throw new RouteNotFoundException("No se encontró ruta para: " + path);
        }
        if (entry.handlerMethod() == null) {
            throw new RuntimeException("Configuración corrupta: HandlerMethod es nulo");
        }
    }


}
