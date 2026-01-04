package com.framework.web;

import com.framework.annotations.GetMapping;
import com.framework.annotations.RequestParam;
import com.framework.context.ApplicationContext;
import com.framework.util.SimpleTypeConverter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class DispatcherHandler implements HttpHandler {

    private final ApplicationContext context;
    private final SimpleTypeConverter typeConverter = new SimpleTypeConverter();

    private final List<RouteEntry> routeEntries = new ArrayList<>();

    public DispatcherHandler(ApplicationContext context) {
        this.context = context;

        loadRoutes();

    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();

        Map<String, String> queryParams = getRequestParams(query);

        System.out.println("Received " + method + " request for " + path);

        RouteEntry entry = getRouteEntry(method, path);

            if (entry == null) {
                System.out.println("❌ Ruta no encontrada para: " + path);
                sendError(exchange, 404, "404 Not Found - Ruta no registrada");
                return;
            }

            if (entry.handlerMethod() == null) {
                sendError(exchange, 500, "HandlerMethod nulo en RouteEntry");
                return;
            }

        Object result = getObjectResult(entry.handlerMethod(), queryParams);

        String responseBody = result.toString();

        byte[] responseBytes = responseBody.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(200, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();

        } catch (Exception e) {
            System.err.println("💥 ERROR NO CONTROLADO:");
            e.printStackTrace();

            try {
                sendError(exchange, 500, "Internal Error: " + e.toString());
            } catch (IOException ioException) {

            }

    }
    }
    private void sendError(HttpExchange exchange, int code, String message) throws IOException {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private RouteEntry getRouteEntry(String method, String path) {

        for(RouteEntry entry : routeEntries) {
            if(entry.matches(method,path) && entry.httpMethod().equalsIgnoreCase(method)) {
                System.out.println("Matched route for " + path + " with pattern " + entry.urlPattern().pattern());
                return entry;
            }
        }

        return null;

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

    private Object getObjectResult(HandlerMethod handlerMethod, Map<String, String> requestParams) throws IllegalAccessException, InvocationTargetException {
        Method methodToInvoke = handlerMethod.method();
        System.out.println("Invoking method: " + methodToInvoke.getName());
        Object controllerInstance = handlerMethod.controller();
        System.out.println("On controller: " + controllerInstance.getClass().getName());
        Object[] parameters = resolveMethodParameters(methodToInvoke, requestParams);

        methodToInvoke.setAccessible(true);

        return methodToInvoke.invoke(controllerInstance, parameters);
    }

    private Object[] resolveMethodParameters(Method method, Map<String, String> requestParams) {

        Parameter[] parameters = method.getParameters();

        Object[] params = new Object[parameters.length];

        for(int i = 0; i < parameters.length; i++ ){
            Parameter parameter = parameters[i];

            if(parameter.isAnnotationPresent(RequestParam.class)){
                RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                String paramName = requestParam.value();
                String paramValue = requestParams.get(paramName);
                params[i] = typeConverter.convert(parameter.getType(), paramValue);
            }
        }

        return params;
    }

    private boolean evaluateNotNull(HttpExchange exchange, HandlerMethod handlerMethod) throws IOException {
        if(handlerMethod == null){
            String response = "404 Not Found";
            exchange.sendResponseHeaders(404, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
            return true;
        }
        return false;
    }


    private void loadRoutes () {
        List<Class<?>> controllers = context.getRegisteredControllers();

        for(Class<?> controller : controllers){
            Object instanceController = context.getBean(controller);

            for(Method method : instanceController.getClass().getMethods()){
                if(method.isAnnotationPresent(GetMapping.class)){
                    GetMapping getMapping = method.getAnnotation(GetMapping.class);
                    String path = getMapping.value();

                    String regex = "^" + path.replaceAll("\\{[^}]+\\}", "([^/]+)") + "[/?]?$";

                    Pattern pattern = Pattern.compile(regex);
                    String methodD = "GET";
                    routeEntries.add(new RouteEntry(methodD, pattern, new HandlerMethod(instanceController, method)));

                    System.out.println("Mapped GET " + path + " to " + method.getName() + " in " + controller.getSimpleName());
                }
            }

        }
    }

}
