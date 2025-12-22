package com.framework.resolvers;

import com.framework.annotations.Qualifier;
import com.framework.context.BeanRegistry;
import com.framework.context.SimpleBeanFactory;

import java.lang.reflect.Parameter;

public class QualifierResolver implements ArgumentResolver{
   private final BeanRegistry beanRegistry;
   private final SimpleBeanFactory simpleBeanFactory;

   public QualifierResolver(BeanRegistry beanRegistry, SimpleBeanFactory simpleBeanFactory) {
         this.beanRegistry = beanRegistry;
         this.simpleBeanFactory = simpleBeanFactory;
   }

    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(Qualifier.class);
    }

    @Override
    public Object resolve(Parameter parameter) {

       String nombre = parameter.getAnnotation(Qualifier.class).value();
       Class<?> tipo = beanRegistry.findClassByName(nombre);
       return simpleBeanFactory.getBean(tipo);
    }
}
