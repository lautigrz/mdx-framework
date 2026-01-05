package com.framework.web;
import com.framework.context.ApplicationContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
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

            logger.log(Level.INFO, "Received " + method + " request for " + path);

            RouteEntry entry = routeRegistry.getRoute(method, path);

            validateRoute(entry, path);

            Map<String, String> queryParams = getRequestParams(query);
            Map<String, String> pathParams = getPathVariables(entry, path);

            Object result = handlerAdapter.execute(entry, queryParams, pathParams);

            sendResponse(exchange, 200, result);

        } catch (RouteNotFoundException e) {
            logger.warning(e.getMessage());

            sendResponse(exchange, 404, "Not Found: " + e.getMessage());

        } catch (IllegalArgumentException e) {
            logger.warning(e.getMessage());

            sendResponse(exchange, 400, "Bad Request: " + e.getMessage());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error crítico en el servidor", e);
            e.printStackTrace();

            sendResponse(exchange, 500, "Internal Server Error: Algo salió mal.");
        }

    }

    private void sendResponse(HttpExchange exchange, int code,Object result) throws IOException {
        String responseBody = result.toString();

        byte[] responseBytes = responseBody.getBytes(StandardCharsets.UTF_8);

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
    public static class RouteNotFoundException extends RuntimeException {
        public RouteNotFoundException(String msg) { super(msg); }
    }

}
