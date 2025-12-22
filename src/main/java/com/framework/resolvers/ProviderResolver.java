package com.framework.resolvers;
import com.framework.context.Provider;
import com.framework.context.SimpleBeanFactory;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ProviderResolver implements ArgumentResolver {

    private SimpleBeanFactory beanFactory;

    public ProviderResolver(SimpleBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public boolean supports(Parameter parameter) {
        return parameter.getType().equals(Provider.class);
    }

    @Override
    public Object resolve(Parameter parameter) {

        ParameterizedType parameterType = (ParameterizedType) parameter.getParameterizedType();

        Type typeArgument = parameterType.getActualTypeArguments()[0];

        Class<?> clazz = (Class<?>) typeArgument;

        return (Provider<Object>) () -> beanFactory.getBean(clazz);
    }
}
