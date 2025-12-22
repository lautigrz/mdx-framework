package com.framework.context;

public interface BeanFactory {
    <T> T getBean(Class<T> beanClass);
}
