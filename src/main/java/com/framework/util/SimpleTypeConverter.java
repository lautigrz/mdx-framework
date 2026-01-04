package com.framework.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SimpleTypeConverter {
    private final Map<Class<?>, Function<String, Object>> converts = new HashMap<>();

    public SimpleTypeConverter() {
        converts.put(Integer.class, Integer::valueOf);
        converts.put(int.class, Integer::valueOf);
        converts.put(Long.class, Long::valueOf);
        converts.put(long.class, Long::valueOf);
        converts.put(Double.class, Double::valueOf);
        converts.put(double.class, Double::valueOf);
        converts.put(Float.class, Float::valueOf);
        converts.put(float.class, Float::valueOf);
        converts.put(Boolean.class, Boolean::valueOf);
        converts.put(boolean.class, Boolean::valueOf);
        converts.put(String.class, s -> s);
    }

    public <T> T convert (Class<T> targetType, String value){

        if (value == null) {
            if (targetType == int.class || targetType == long.class || targetType == double.class) {
                return (T) Integer.valueOf(0);
            }
            if (targetType == boolean.class) {
                return (T) Boolean.FALSE;
            }
            return null;
        }

        Function<String, Object> converter = converts.get(targetType);

        if (converter != null) {
            return (T) converter.apply(value);
        }

        throw new IllegalArgumentException("No hay convertidor para: " + targetType.getName());
    }

}
