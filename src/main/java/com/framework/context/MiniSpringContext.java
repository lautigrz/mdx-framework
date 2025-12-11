package com.framework.context;

import com.framework.annotations.PostConstruct;
import com.framework.config.PropertySource;
import com.framework.resolvers.ArgumentResolver;
import com.framework.resolvers.QualifierResolver;
import com.framework.resolvers.TypeResolver;
import com.framework.resolvers.ValueResolver;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class MiniSpringContext implements ApplicationContext{
    private final PropertySource config;
    private final BeanRegistry registry;
    private final Map<Class<?>, Object> singletonCache = new HashMap<Class<?>, Object>();
    private final List<ArgumentResolver> resolvers = new ArrayList<>();

    public MiniSpringContext(PropertySource config, List<Class<?>> clases) {
        this.config = config;
        this.registry = new BeanRegistry(clases);
        this.resolvers.add(new ValueResolver(this.config));
        this.resolvers.add(new QualifierResolver(this.registry, this));
        this.resolvers.add(new TypeResolver(this));
    }

    @Override
    public <T> T getBean(Class<T> claseSolicitada) {

        try {
            Class<?> claseImpl = registry.encontrarImplementacion(claseSolicitada);
            if(singletonCache.containsKey(claseImpl)){
                return (T) singletonCache.get(claseImpl);
            }

            T instancia = crearInstancia(claseImpl);

            singletonCache.put(claseImpl, instancia);

            return instancia;

        }catch (Exception e) {
            throw new RuntimeException("Error creando bean: " + claseSolicitada.getSimpleName(), e);
        }

    }
    private <T> T crearInstancia(Class<?> claseConcreta) throws Exception {
        Constructor<?> constructor = claseConcreta.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        Object[] argumentos = resolverArgumentos(constructor);

        T instancia = (T) constructor.newInstance(argumentos);
        inicializarBean(instancia);
        return instancia;

    }
    private Object[] resolverArgumentos(Constructor<?> constructor) {
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

    private void inicializarBean(Object instancia) throws Exception {
        for (Method m : instancia.getClass().getDeclaredMethods()) {
            if (m.isAnnotationPresent(PostConstruct.class)) {
                m.setAccessible(true);
                m.invoke(instancia);
            }
        }
    }
}
