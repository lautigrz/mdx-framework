package com.framework.web;

import com.framework.enums.HttpMethod;

import java.util.List;
import java.util.regex.Pattern;

public record RouteEntry(
        HttpMethod httpMethod,
        Pattern urlPattern,
        HandlerMethod handlerMethod,
        List<String> pathVariables
) {

    public boolean matches(HttpMethod httpMethod, String path) {

        if (!this.httpMethod.equals(httpMethod)){
            return false;
        }

        return this.urlPattern.matcher(path).matches();
    }
}
