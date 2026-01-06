package com.framework.web;

import com.framework.annotations.Get;
import com.framework.annotations.Post;
import com.framework.context.ApplicationContext;
import com.framework.enums.HttpMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteRegistry {
    private static final Logger logger = Logger.getLogger(RouteRegistry.class.getName());

    private final List<RouteEntry> routeEntries = new ArrayList<>();
    private final ApplicationContext context;

    public RouteRegistry(ApplicationContext applicationContext) {
        this.context = applicationContext;
        loadRoutes();
    }


    public RouteEntry getRoute(HttpMethod method, String path) {
        for(RouteEntry entry : routeEntries) {
            if(entry.matches(method, path)) {
                return entry;
            }
        }
        return null;
    }


    private void loadRoutes() {
        List<Class<?>> controllers = context.getRegisteredControllers();

        for(Class<?> controller : controllers){
            Object instanceController = context.getBean(controller);

            for(Method method : instanceController.getClass().getMethods()){
                if(method.isAnnotationPresent(Get.class)){
                    Get annot = method.getAnnotation(Get.class);
                    registerRoute(instanceController, method, annot.value(), HttpMethod.GET);
                }
                else if(method.isAnnotationPresent(Post.class)){
                    Post annot = method.getAnnotation(Post.class);
                    registerRoute(instanceController, method, annot.value(), HttpMethod.POST);
                }
            }

        }
    }

    private void registerRoute(Object controllerInstance, Method method, String path, HttpMethod httpMethod) {

        List<String> paramNames = new ArrayList<>();

        Pattern nameExtractor = Pattern.compile("\\{([^}]+)\\}");
        Matcher matcher = nameExtractor.matcher(path);

        while(matcher.find()) {
            logger.info("Found path variable: " + matcher.group(1));
            paramNames.add(matcher.group(1));
        }

        String regex = "^" + path.replaceAll("\\{[^}]+\\}", "([^/]+)") + "[/?]?$";
        Pattern pattern = Pattern.compile(regex);

        routeEntries.add(new RouteEntry(httpMethod, pattern, new HandlerMethod(controllerInstance, method),paramNames));

        logger.info("Mapped " + httpMethod + " " + path + " to " + method.getName());
    }
}
