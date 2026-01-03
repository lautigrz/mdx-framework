package com.framework.web;

import com.framework.annotations.GetMapping;
import com.framework.context.ApplicationContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

public class DispatcherHandler implements HttpHandler {

    private final ApplicationContext context;
    private final HashMap<String, HandlerMethod> handlerMethods = new HashMap<>();

    public DispatcherHandler(ApplicationContext context) {
        this.context = context;
        laodControllers();
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        String key = method + ":" + path;
        System.out.println("Received " + method + " request for " + path);


        HandlerMethod handlerMethod = handlerMethods.get(key);

        if(handlerMethod == null){
            String response = "404 Not Found";
            exchange.sendResponseHeaders(404, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
            return;
        }

        Method methodToInvoke = handlerMethod.method();
        Object controllerInstance = handlerMethod.controller();

        methodToInvoke.setAccessible(true);
        Object result = methodToInvoke.invoke(controllerInstance);

        String respondeBody = result.toString();

        byte[] responseBytes = respondeBody.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(200, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }


    }


    private void laodControllers() {

        List<Class<?>> controllers = context.getRegisteredControllers();

        for(Class<?> controller : controllers){
            Object instanceController = context.getBean(controller);

            for(Method method : instanceController.getClass().getMethods()){
                if(method.isAnnotationPresent(GetMapping.class)){
                    GetMapping getMapping = method.getAnnotation(GetMapping.class);
                    String path = "GET:" + getMapping.value();
                    handlerMethods.put(path, new HandlerMethod(instanceController, method));
                    System.out.println("Mapped GET " + path + " to " + method.getName() + " in " + controller.getSimpleName());
                }
            }

        }



    }
}
