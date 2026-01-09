package com.framework.context;

import com.framework.annotations.PostConstruct;
import com.framework.config.PropertySource;
import com.framework.resolvers.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.logging.Logger;

public class SimpleBeanFactory implements BeanFactory {
    private BeanRegistry registry;
    private final PropertySource propertySource;
    private final Map<Class<?>, Object> singletonCache = new HashMap<Class<?>, Object>();
    private final List<ArgumentResolver> resolvers = new ArrayList<>();
    private final Logger logger = Logger.getLogger(SimpleBeanFactory.class.getName());

    public SimpleBeanFactory(PropertySource propertySource) {
        this.propertySource = propertySource;
        initializeResolvers(propertySource);
    }
    public List<Class<?>> getRegisteredControllers(){
        return (registry != null) ? registry.extractClassesControllers() : Collections.emptyList();
    }

    public <T> void registerManualBean(Class<T> type, T instance) {
        singletonCache.put(type, instance);
    }

    @Override
    public <T> T getBean(Class<T> type) {

        T cachedInstance = (T) searchInCache(type);

        if (cachedInstance != null) {
            return cachedInstance;
        }

        try {
            Class<?> implementationClass = registry.findImplementation(type);
            if(singletonCache.containsKey(implementationClass)){
                return (T) singletonCache.get(implementationClass);
            }

            T instance = createInstance(implementationClass);

            singletonCache.put(implementationClass, instance);

            if(!implementationClass.equals(type)) {
                logger.info("Registrando bean en caché por tipo: " + type.getSimpleName());
                singletonCache.put(type, instance);
            }

            return instance;

        }catch (Exception e) {
            throw new RuntimeException("Error creando bean: " + type.getSimpleName(), e);
        }

    }

    public void instantiate(List<Class<?>> scannedClasses) {

        this.registry = new BeanRegistry(scannedClasses);

        for (Class<?> clazz : scannedClasses) {

            if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
                continue;
            }

            try {
                getBean(clazz);
            } catch (Exception e) {

                System.err.println("Error instanciando bean al arranque: " + clazz.getName());
                e.printStackTrace();
            }
        }

    }

    private <T> Object searchInCache(Class<T> type) {
        if (singletonCache.containsKey(type)) {
            logger.info("Bean encontrado en caché por clase: " + type.getSimpleName());
            return type.cast(singletonCache.get(type));
        }
        Object foundInstance = null;

        for (Object instance : singletonCache.values()) {
            if (type.isInstance(instance)) {
                foundInstance = instance;
                break;
            }
        }

        if(foundInstance != null){
            logger.info("Bean encontrado en caché por tipo (LENTO). Creando atajo para: " + type.getSimpleName());
            singletonCache.put(type, foundInstance);
            return foundInstance;
        }

        if (registry == null) {
            throw new RuntimeException("El BeanFactory aún no ha cargado las definiciones (Fase de escaneo incompleta).");
        }

        return null;
    }

    private <T> T createInstance(Class<?> concreteClass) throws Exception {
        Constructor<?> constructor = findBestConstructor(concreteClass);
        constructor.setAccessible(true);

        Object[] arguments = resolveArguments(constructor);

        T instance = (T) constructor.newInstance(arguments);
        initializeBean(instance);
        return instance;

    }
    private Constructor<?> findBestConstructor(Class<?> concreteClass) {
        Constructor<?>[] constructors = concreteClass.getDeclaredConstructors();
        if (constructors.length == 1) return constructors[0];

        return Arrays.stream(constructors)
                .max(Comparator.comparingInt(Constructor::getParameterCount))
                .orElseThrow(() -> new RuntimeException("No hay constructor viable en " + concreteClass.getName()));
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

}
