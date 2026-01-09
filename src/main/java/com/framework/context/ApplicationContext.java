package com.framework.context;

public interface ApplicationContext extends BeanFactory {
    void refresh();
    <T> void registerSingleton(Class<T> type, T instance);
}
