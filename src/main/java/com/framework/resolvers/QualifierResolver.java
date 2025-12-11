package com.framework.resolvers;

import com.framework.annotations.Qualifier;
import com.framework.context.ApplicationContext;
import com.framework.context.BeanRegistry;

import java.lang.reflect.Parameter;

public class QualifierResolver implements ArgumentResolver{
   private final BeanRegistry beanRegistry;
   private final ApplicationContext applicationContext;

   public QualifierResolver(BeanRegistry beanRegistry, ApplicationContext applicationContext) {
         this.beanRegistry = beanRegistry;
         this.applicationContext = applicationContext;
   }

    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(Qualifier.class);
    }

    @Override
    public Object resolve(Parameter parameter) {
       String nombre = parameter.getAnnotation(Qualifier.class).value();
       Class<?> tipo = beanRegistry.encontrarClasePorNombre(nombre);
       return applicationContext.getBean(tipo);
    }
}
