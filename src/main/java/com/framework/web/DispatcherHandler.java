package com.framework.web;
import com.framework.context.ApplicationContext;
import com.framework.enums.HttpMethod;
import com.framework.exception.BadRequestException;
import com.framework.exception.RouteNotFoundException;
import com.framework.util.SimpleSerialization;
import com.framework.web.response.HttpResponseWriter;
import com.framework.web.response.ResponseConverter;
import com.framework.web.response.ResponsePayload;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;


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
            String query = exchange.getRequestURI().getQuery();
            String body = getBody(exchange).toString();

            logger.log(Level.INFO, "Received " + method + " request for " + path);

            RouteEntry entry = routeRegistry.getRoute(HttpMethod.valueOf(method), path);

            validateRoute(entry, path);

            Map<String, String> queryParams = getRequestParams(query);
            Map<String, String> pathParams = getPathVariables(entry, path);

            WebRequest webRequest = new WebRequest(queryParams, pathParams, body);

            Object result = handlerAdapter.execute(entry, webRequest);

            ResponsePayload payload = responseConverter.convert(result);

            responseWriter.writeResponse(exchange, 200, payload);

        } catch (RouteNotFoundException e) {

            ResponsePayload payload = responseConverter.convertError(404,e.getMessage());

            responseWriter.writeResponse(exchange, 404, payload);

        } catch (BadRequestException e) {

            ResponsePayload payload = responseConverter.convertError(400,e.getMessage());

            responseWriter.writeResponse(exchange,400,payload);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error crítico en el servidor", e);

            ResponsePayload payload = responseConverter.convertError(500,e.getMessage());

            responseWriter.writeResponse(exchange,500,payload);
        }

    }

    private void validateRoute(RouteEntry entry, String path) {
        if (entry == null) {
            throw new RouteNotFoundException("No se encontró ruta para: " + path);
        }
        if (entry.handlerMethod() == null) {
            throw new RuntimeException("Configuración corrupta: HandlerMethod es nulo");
        }
    }

    private StringBuilder getBody(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));

        StringBuilder body = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }
        return body;
    }

    private Map<String, String> getPathVariables(RouteEntry entry, String path) {
        Map<String, String> variables = new HashMap<>();

        Matcher matcher = entry.urlPattern().matcher(path);

        if(matcher.matches()){
            for(int i = 0; i < entry.pathVariables().size(); i++) {
                String paramName = entry.pathVariables().get(i);
                String paramValue = matcher.group(i + 1);
                variables.put(paramName, paramValue);
            }

        }
        return variables;

    }

    private Map<String, String> getRequestParams(String query) {
        Map<String, String> requestParams = new HashMap<>();
        if (query == null || query.isEmpty()) return requestParams;

        String[] pairs = query.split("&");

        for(String pair : pairs) {
            String[] keyValue = pair.split("=");
            if(keyValue.length == 2){
                requestParams.put(keyValue[0], keyValue[1]);
            }
        }
        return requestParams;
    }

}
