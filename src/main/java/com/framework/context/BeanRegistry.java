package com.framework.context;

import com.framework.annotations.RestResource;

import java.util.List;

public class BeanRegistry {

    private final List<Class<?>> registeredBeans;

    public BeanRegistry(List<Class<?>> registeredBeans) {
        this.registeredBeans = registeredBeans;
    }

    public Class<?> findImplementation(Class<?> interfaceClass) {
        List<Class<?>> candidates = registeredBeans.stream()
                .filter(clase -> !clase.isInterface() && interfaceClass.isAssignableFrom(clase))
                .toList();

        if(candidates.isEmpty()) {
            throw new RuntimeException("No se encontró ninguna implementación para la clase solicitada: " + interfaceClass.getName());
        } else if(candidates.size() > 1) {
            throw new RuntimeException("Se encontraron múltiples implementaciones para la clase solicitada: " + interfaceClass.getName());
        } else {
            return candidates.get(0);
        }

    }


    public Class<?> findClassByName(String className) {
        return registeredBeans.stream()
                .filter(clase -> clase.getSimpleName().equals(className))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No se encontró ninguna clase con el nombre: " + className));
    }


    public List<Class<?>> extractClassesControllers() {
        return registeredBeans.stream().filter(
                clase -> clase.isAnnotationPresent(RestResource.class))
                .toList();
    }



}
