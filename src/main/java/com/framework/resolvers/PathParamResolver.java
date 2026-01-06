package com.framework.resolvers;

import com.framework.annotations.PathParam;
import com.framework.util.SimpleTypeConverter;
import com.framework.web.WebRequest;

import java.lang.reflect.Parameter;

public class PathParamResolver implements WebArgumentResolver{

    private final SimpleTypeConverter typeConverter;

    public PathParamResolver() {
        this.typeConverter = new SimpleTypeConverter();
    }


    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(PathParam.class);
    }

    @Override
    public Object resolve(Parameter parameter, WebRequest webRequest) throws Exception {

        String pathVariable = parameter.getAnnotation(PathParam.class).value();
        String value = webRequest.pathVariables().get(pathVariable);

        return this.typeConverter.convert(parameter.getType(), value);

    }
}
