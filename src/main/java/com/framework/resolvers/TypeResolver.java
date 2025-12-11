package com.framework.resolvers;

import com.framework.context.ApplicationContext;

import java.lang.reflect.Parameter;

public class TypeResolver implements ArgumentResolver{

   private final ApplicationContext context;
    public TypeResolver(ApplicationContext context) {
         this.context = context;
    }

    @Override
    public boolean supports(Parameter parameter) {
        return true;
    }

    @Override
    public Object resolve(Parameter parameter) {
        return context.getBean(parameter.getType());
    }
}
