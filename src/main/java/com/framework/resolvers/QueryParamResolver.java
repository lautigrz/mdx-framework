package com.framework.resolvers;

import com.framework.annotations.QueryParam;
import com.framework.util.SimpleTypeConverter;
import com.framework.web.WebRequest;

import java.lang.reflect.Parameter;

public class QueryParamResolver implements WebArgumentResolver{

    private final SimpleTypeConverter typeConverter;

    public QueryParamResolver() {
        this.typeConverter = new SimpleTypeConverter();
    }

    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(QueryParam.class);
    }

    @Override
    public Object resolve(Parameter parameter, WebRequest webRequest) throws Exception {
        String requestParam = parameter.getAnnotation(QueryParam.class).value();
        String value = webRequest.queryParams().get(requestParam);
        return this.typeConverter.convert(parameter.getType(), value);
    }
}
