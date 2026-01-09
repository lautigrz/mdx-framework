package com.framework.context;

import java.util.List;

public interface BeanFactory {
    <T> T getBean(Class<T> beanClass);
    List<Class<?>> getRegisteredControllers();

}
