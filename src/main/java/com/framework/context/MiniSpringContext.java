package com.framework.context;

import com.framework.annotations.PostConstruct;
import com.framework.config.PropertySource;
import com.framework.resolvers.ArgumentResolver;
import com.framework.resolvers.QualifierResolver;
import com.framework.resolvers.TypeResolver;
import com.framework.resolvers.ValueResolver;
import com.framework.scanners.ComponentScanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class MiniSpringContext implements ApplicationContext{
    private final PropertySource config;
    private final BeanRegistry registry;
    private final Map<Class<?>, Object> singletonCache = new HashMap<Class<?>, Object>();
    private final List<ArgumentResolver> resolvers = new ArrayList<>();

    public MiniSpringContext(PropertySource config, String basePackage){
        this.config = config;
        ComponentScanner scanner = new ComponentScanner();
        List<Class<?>> classes = scanner.scan(basePackage);
        this.registry = new BeanRegistry(classes);
        this.resolvers.add(new ValueResolver(this.config));
        this.resolvers.add(new QualifierResolver(this.registry, this));
        this.resolvers.add(new TypeResolver(this));
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
}
