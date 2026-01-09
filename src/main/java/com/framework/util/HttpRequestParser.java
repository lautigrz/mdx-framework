package com.framework.util;

import com.framework.web.RouteEntry;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

public class HttpRequestParser {

    public static String parseBody(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));

        StringBuilder body = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }
        return body.toString();
    }

    public static Map<String, String> parsePathVariables(RouteEntry entry, String path){
        Map<String, String> variables = new HashMap<>();

        Matcher matcher = entry.urlPattern().matcher(path);

        if(matcher.matches()){
            for(int i = 0; i < entry.pathVariables().size(); i++) {
                String paramName = entry.pathVariables().get(i);
                String paramValue = matcher.group(i + 1);
                variables.put(paramName, paramValue);
            }

        }
        return variables;
    }

    public static Map<String, String> parseQueryParameters(String query){
        Map<String, String> requestParams = new HashMap<>();
        if (query == null || query.isEmpty()) return requestParams;

        String[] pairs = query.split("&");

        for(String pair : pairs) {
            String[] keyValue = pair.split("=");
            if(keyValue.length == 2){
                requestParams.put(keyValue[0], keyValue[1]);
            }
        }
        return requestParams;
    }
}
