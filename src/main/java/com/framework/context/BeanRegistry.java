package com.framework.context;

import java.util.List;

public class BeanRegistry {

    private final List<Class<?>> registeredBeans;

    public BeanRegistry(List<Class<?>> registeredBeans) {
        this.registeredBeans = registeredBeans;
    }

    public Class<?> encontrarImplementacion(Class<?> claseSolicitada) {
        List<Class<?>> candidatas = registeredBeans.stream()
                .filter(clase -> !clase.isInterface() && claseSolicitada.isAssignableFrom(clase))
                .toList();

        if(candidatas.isEmpty()) {
            throw new RuntimeException("No se encontró ninguna implementación para la clase solicitada: " + claseSolicitada.getName());
        } else if(candidatas.size() > 1) {
            throw new RuntimeException("Se encontraron múltiples implementaciones para la clase solicitada: " + claseSolicitada.getName());
        } else {
            return candidatas.get(0);
        }

    }


    public Class<?> encontrarClasePorNombre(String nombreClase) {
        return registeredBeans.stream()
                .filter(clase -> clase.getSimpleName().equals(nombreClase))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No se encontró ninguna clase con el nombre: " + nombreClase));
    }

}
