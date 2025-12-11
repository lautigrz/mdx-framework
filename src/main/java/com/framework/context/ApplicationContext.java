package com.framework.context;

public interface ApplicationContext {
    <T> T getBean(Class<T> claseSolicitada);
}
