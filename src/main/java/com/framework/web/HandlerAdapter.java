package com.framework.web;

import com.framework.annotations.PathVariable;
import com.framework.annotations.RequestParam;
import com.framework.util.SimpleTypeConverter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

public class HandlerAdapter {

    private final SimpleTypeConverter typeConverter = new SimpleTypeConverter();

    public Object execute(RouteEntry entry, Map<String, String> queryParams, Map<String, String> pathVariables) throws InvocationTargetException, IllegalAccessException {
        HandlerMethod handlerMethod = entry.handlerMethod();
        Method method = handlerMethod.method();

        Object controller = handlerMethod.controller();

        Object[] args = resolveParameters(method, queryParams, pathVariables);

        method.setAccessible(true);
        return method.invoke(controller, args);
    }

    private Object[] resolveParameters(Method method, Map<String, String> queryParams, Map<String, String> pathVariables) {

        Parameter[] parameters = method.getParameters();

        Object[] params = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];

            if (parameter.isAnnotationPresent(RequestParam.class)) {
                String requestParam = parameter.getAnnotation(RequestParam.class).value();

                params[i] = processAndConvert(requestParam, queryParams, parameter.getType());
            }

            else if (parameter.isAnnotationPresent(PathVariable.class)) {
                String pathVariable = parameter.getAnnotation(PathVariable.class).value();

                params[i] = processAndConvert(pathVariable, pathVariables, parameter.getType());
            }

        }
        return params;
    }


    private Object processAndConvert(String key, Map<String, String> sourceMap, Class<?> targetType) {
        String value = sourceMap.get(key);
        return typeConverter.convert(targetType, value);
    }

}
