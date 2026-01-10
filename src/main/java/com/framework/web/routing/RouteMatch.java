package com.framework.web.routing;

import com.framework.web.RouteEntry;

import java.util.Map;

public class RouteMatch {

    public RouteEntry routeEntry;

    public Map<String, String> pathVariables;

    public RouteMatch(RouteEntry entry, Map<String, String> pathVariables) {
        this.routeEntry = entry;
        this.pathVariables = pathVariables;
    }
}
