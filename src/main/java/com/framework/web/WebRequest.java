package com.framework.web;

import java.util.Map;

public record WebRequest(
        Map<String, String> queryParams,
        Map<String, String> pathVariables,
        String body) {

}
