package com.framework.web;

import com.framework.annotations.FromBody;
import com.framework.annotations.PathParam;
import com.framework.annotations.QueryParam;
import com.framework.exception.BadRequestException;
import com.framework.resolvers.BodyResolver;
import com.framework.resolvers.PathParamResolver;
import com.framework.resolvers.QueryParamResolver;
import com.framework.resolvers.WebArgumentResolver;
import com.framework.util.SimpleSerialization;
import com.framework.util.SimpleTypeConverter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HandlerAdapter {

    private final List<WebArgumentResolver> webResolvers;

    public HandlerAdapter() {

        this.webResolvers = List.of(
                new QueryParamResolver(),
                new PathParamResolver(),
                new BodyResolver()
        );
    }
    public Object execute(RouteEntry entry, WebRequest webRequest) throws Exception {
        HandlerMethod handlerMethod = entry.handlerMethod();
        Method method = handlerMethod.method();

        Object controller = handlerMethod.controller();

        Object[] args = resolveParameters(method, webRequest);

        method.setAccessible(true);
        return method.invoke(controller, args);
    }

    private Object[] resolveParameters(Method method, WebRequest webRequest) throws Exception {

        Parameter[] parameters = method.getParameters();

        Object[] params = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];

            WebArgumentResolver resolver = findWebResolver(parameter);

            if (resolver != null) {
                params[i] = resolver.resolve(parameter, webRequest);
            }else {
                params[i] = null;
            }

        }
        return params;
    }


  private WebArgumentResolver findWebResolver(Parameter parameter) {
        for (WebArgumentResolver resolver : webResolvers) {
            if (resolver.supports(parameter)) {
                return resolver;
            }
        }
        return null;
    }

}
