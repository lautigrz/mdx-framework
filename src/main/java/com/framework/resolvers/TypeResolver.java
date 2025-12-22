package com.framework.resolvers;

import com.framework.context.ApplicationContext;
import com.framework.context.SimpleBeanFactory;

import java.lang.reflect.Parameter;

public class TypeResolver implements ArgumentResolver{

   private final SimpleBeanFactory simpleBeanFactory;
    public TypeResolver(SimpleBeanFactory simpleBeanFactory) {
         this.simpleBeanFactory = simpleBeanFactory;
    }

    @Override
    public boolean supports(Parameter parameter) {
        return true;
    }

    @Override
    public Object resolve(Parameter parameter) {
        return simpleBeanFactory.getBean(parameter.getType());
    }
}
