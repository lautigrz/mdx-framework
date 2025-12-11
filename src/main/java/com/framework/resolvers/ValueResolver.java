package com.framework.resolvers;

import com.framework.annotations.Value;
import com.framework.config.PropertySource;

import java.lang.reflect.Parameter;

public class ValueResolver implements ArgumentResolver{

    private final PropertySource propertySource;
    public ValueResolver(PropertySource propertySource) {
        this.propertySource = propertySource;
    }

    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(Value.class);
    }

    @Override
    public Object resolve(Parameter parameter) {
        String clave = parameter.getAnnotation(Value.class).value();
        return propertySource.getProperty(clave);
    }
}
