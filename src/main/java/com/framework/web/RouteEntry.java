package com.framework.web;

import com.framework.enums.HttpMethod;


public record RouteEntry(
        HttpMethod httpMethod,
        HandlerMethod handlerMethod
) {

}
