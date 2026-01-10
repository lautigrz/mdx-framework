package com.framework.web.routing;

import com.framework.web.RouteEntry;

import java.util.HashMap;
import java.util.Map;

public class RouteNode {
    private final Map<String, RouteNode> children = new HashMap<>();

    private RouteNode wildcardChild;
    private String wildcardParamName;

    private RouteEntry routeEntry;

    public RouteNode() {

    }

    public void insert(String[] parts, int index, RouteEntry routeEntry) {
        if(index == parts.length){
            this.routeEntry = routeEntry;
            return;
        }

        String part = parts[index];

        if(part.startsWith("{") && part.endsWith("}")){
            if(wildcardChild == null){
                wildcardChild = new RouteNode();
                wildcardParamName = part.substring(1, part.length() - 1);
            }
            wildcardChild.insert(parts, index + 1, routeEntry);
    } else {
            children.putIfAbsent(part, new RouteNode());
            children.get(part).insert(parts, index + 1, routeEntry);
        }
    }

    public RouteMatch find(String[] parts, int index) {

        if (index == parts.length) {

            if (this.routeEntry != null) {
                return new RouteMatch(this.routeEntry, new HashMap<>());
            }
            return null;
        }

        String part = parts[index];

        RouteNode child = children.get(part);
        if (child != null) {
            RouteMatch match = child.find(parts, index + 1);
            if (match != null) return match;
        }

        if (wildcardChild != null) {
            RouteMatch match = wildcardChild.find(parts, index + 1);
            if (match != null) {

                match.pathVariables.put(wildcardParamName, part);
                return match;
            }
        }

        return null;
    }
}
