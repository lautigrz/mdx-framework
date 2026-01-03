package com.framework.context;

import com.framework.annotations.PostConstruct;
import com.framework.config.PropertySource;
import com.framework.resolvers.*;
import com.framework.scanners.ComponentScanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class SimpleBeanFactory implements BeanFactory {
    private final BeanRegistry registry;
    private final Map<Class<?>, Object> singletonCache = new HashMap<Class<?>, Object>();
    private final List<ArgumentResolver> resolvers = new ArrayList<>();

    public SimpleBeanFactory(List<Class<?>> classes, PropertySource propertySource) {
        this.registry = new BeanRegistry(classes);
        initializeResolvers(propertySource);
    }

    @Override
    public <T> T getBean(Class<T> type) {

        try {
            Class<?> implementationClass = registry.findImplementation(type);
            if(singletonCache.containsKey(implementationClass)){
                return (T) singletonCache.get(implementationClass);
            }

            T instance = createInstance(implementationClass);

            singletonCache.put(implementationClass, instance);

            return instance;

        }catch (Exception e) {
            throw new RuntimeException("Error creando bean: " + type.getSimpleName(), e);
        }

    }
    private <T> T createInstance(Class<?> concreteClass) throws Exception {
        Constructor<?> constructor = concreteClass.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        Object[] arguments = resolveArguments(constructor);

        T instance = (T) constructor.newInstance(arguments);
        initializeBean(instance);
        return instance;

    }
    private Object[] resolveArguments(Constructor<?> constructor) {
        return Arrays.stream(constructor.getParameters())
                .map(this::resolveSingleParameter)
                .toArray();
    }
    private Object resolveSingleParameter(Parameter p) {

        for (ArgumentResolver resolver : resolvers) {
            if (resolver.supports(p)) {
                return resolver.resolve(p);
            }
        }
        throw new RuntimeException("No se pudo resolver el parámetro: " + p.getName());
    }

    private void initializeBean(Object bean) throws Exception {
        for (Method m : bean.getClass().getDeclaredMethods()) {
            if (m.isAnnotationPresent(PostConstruct.class)) {
                m.setAccessible(true);
                m.invoke(bean);
            }
        }
    }

    private void initializeResolvers(PropertySource propertySource) {
        resolvers.add(new QualifierResolver(registry,this));
        resolvers.add(new ValueResolver(propertySource));
        resolvers.add(new ProviderResolver(this));
        resolvers.add(new TypeResolver(this));
    }

    public List<Class<?>> getRegisteredControllers(){
        return registry.extractClassesControllers();
    }
}
