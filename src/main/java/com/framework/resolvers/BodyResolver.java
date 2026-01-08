package com.framework.resolvers;

import com.framework.annotations.FromBody;
import com.framework.exception.BadRequestException;
import com.framework.util.SimpleSerialization;
import com.framework.web.WebRequest;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public class BodyResolver implements WebArgumentResolver{

    private final SimpleSerialization simpleSerialization;

    public BodyResolver() {
        this.simpleSerialization = new SimpleSerialization();
    }

    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(FromBody.class);
    }

    @Override
    public Object resolve(Parameter parameter, WebRequest webRequest) throws Exception {
        try {
            Type fullType = parameter.getParameterizedType();
            return this.simpleSerialization.fromJson(webRequest.body(), fullType);
        }catch (Exception e){
            throw new BadRequestException("Invalid request body ", e);
        }
    }
}
