package com.framework.web;
import com.framework.context.ApplicationContext;
import com.framework.enums.HttpMethod;
import com.framework.exception.BadRequestException;
import com.framework.exception.RouteNotFoundException;
import com.framework.util.SimpleSerialization;
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

    public DispatcherHandler(ApplicationContext context) {
        this.handlerAdapter = new HandlerAdapter();
        this.routeRegistry = new RouteRegistry(context);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            String query = exchange.getRequestURI().getQuery();
            StringBuilder body = getBody(exchange);

            logger.log(Level.INFO, "Received " + method + " request for " + path);

            RouteEntry entry = routeRegistry.getRoute(HttpMethod.valueOf(method), path);

            validateRoute(entry, path);

            Map<String, String> queryParams = getRequestParams(query);
            Map<String, String> pathParams = getPathVariables(entry, path);

            WebRequest webRequest = new WebRequest(queryParams, pathParams, body.toString());

            Object result = handlerAdapter.execute(entry, webRequest);

            sendResponse(exchange, 200, result);

        } catch (RouteNotFoundException e) {
            logger.warning(e.getMessage());

            sendResponse(exchange, 404, "Not Found: " + e.getMessage());

        } catch (BadRequestException e) {
            logger.warning(e.getMessage());

            sendResponse(exchange, 400, "Bad Request: " + e.getMessage());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error crítico en el servidor", e);
            e.printStackTrace();

            sendResponse(exchange, 500, "Internal Server Error: Algo salió mal.");
        }

    }

    private void sendResponse(HttpExchange exchange, int code,Object result) throws IOException {

        String content = "";
        String contentType = "text/plain; charset=UTF-8";

        if(result instanceof String) {
            content = (String) result;
        } else {
            SimpleSerialization serialization = new SimpleSerialization();
            try {
                content = serialization.toJson(result);
                contentType = "application/json; charset=UTF-8";
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error serializando la respuesta a JSON", e);
                content = "Internal Server Error: Error serializando la respuesta.";
                code = 500;
            }
        }

        String responseBody = content;

        byte[] responseBytes = responseBody.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().add("Content-Type", contentType);

        exchange.sendResponseHeaders(code, responseBytes.length);

        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
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
