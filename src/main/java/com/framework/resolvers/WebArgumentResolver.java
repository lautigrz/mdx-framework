package com.framework.resolvers;

import com.framework.web.WebRequest;

import java.lang.reflect.Parameter;

public interface WebArgumentResolver {
    boolean supports(Parameter parameter);
    Object resolve(Parameter parameter, WebRequest webRequest) throws Exception;
}
