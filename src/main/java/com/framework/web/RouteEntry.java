package com.framework.web;

import java.util.List;
import java.util.regex.Pattern;

public record RouteEntry(
        String httpMethod,
        Pattern urlPattern,
        HandlerMethod handlerMethod,
        List<String> pathVariables
) {

    public boolean matches(String method, String path) {

        if (!this.httpMethod.equalsIgnoreCase(method)) {
            return false;
        }

        return this.urlPattern.matcher(path).matches();
    }
}
