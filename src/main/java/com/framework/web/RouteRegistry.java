package com.framework.web;

import com.framework.annotations.Get;
import com.framework.annotations.Post;
import com.framework.context.ApplicationContext;
import com.framework.enums.HttpMethod;
import com.framework.web.routing.RouteMatch;
import com.framework.web.routing.RouteNode;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteRegistry {
    private static final Logger logger = Logger.getLogger(RouteRegistry.class.getName());

    private final Map<HttpMethod, RouteNode> rootNodes = new HashMap<>();
    private final ApplicationContext context;

    private static final Map<Class<? extends Annotation>, HttpMethod> HTTP_ANNOTATIONS = Map.of(
            Get.class, HttpMethod.GET,
            Post.class, HttpMethod.POST

    );
    public RouteRegistry(ApplicationContext applicationContext) {
        this.context = applicationContext;
        for (HttpMethod method : HttpMethod.values()) {
            rootNodes.put(method, new RouteNode());
        }
        loadRoutes();
    }

    public RouteMatch getRoute(HttpMethod method, String path) {
        String cleanPath = path.startsWith("/") ? path.substring(1) : path;
        if (cleanPath.endsWith("/")) cleanPath = cleanPath.substring(0, cleanPath.length() - 1);

        String[] parts = cleanPath.isEmpty() ? new String[0] : cleanPath.split("/");

        RouteNode root = rootNodes.get(method);
        if (root == null) return null;

        return root.find(parts, 0);
    }


    private void loadRoutes() {
        List<Class<?>> controllers = context.getRegisteredControllers();

        for(Class<?> controller : controllers){
            Object instanceController = context.getBean(controller);

            for(Method method : instanceController.getClass().getMethods()){

                for (Map.Entry<Class<? extends Annotation>, HttpMethod> entry : HTTP_ANNOTATIONS.entrySet()) {
                    Class<? extends Annotation> annotationType = entry.getKey();
                    HttpMethod httpMethod = entry.getValue();

                    if(method.isAnnotationPresent(annotationType)) {
                        try {

                            Annotation annotation = method.getAnnotation(annotationType);

                            Method valueMethod = annotationType.getMethod("value");
                            String path = (String) valueMethod.invoke(annotation);

                            registerRoute(instanceController, method, path, httpMethod);

                        } catch (Exception e) {
                            throw new RuntimeException("Error al procesar la ruta en: " + method.getName(), e);
                        }
                    }
                }
            }

        }
    }

    private void registerRoute(Object controllerInstance, Method method, String path, HttpMethod httpMethod) {

        String cleanPath = path.startsWith("/") ? path.substring(1) : path;
        if (cleanPath.endsWith("/")) cleanPath = cleanPath.substring(0, cleanPath.length() - 1);
        String[] parts = cleanPath.isEmpty() ? new String[0] : cleanPath.split("/");

        RouteEntry entry = new RouteEntry(
                httpMethod,
                new HandlerMethod(controllerInstance, method)
        );

        rootNodes.get(httpMethod).insert(parts, 0, entry);

        String target = controllerInstance.getClass().getSimpleName() + "." + method.getName();
        logger.info(String.format("Mapped {%s %s} -> %s", httpMethod, path, target));
    }
}
