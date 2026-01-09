package com.framework.context;

import com.framework.config.PropertiesFileSource;
import com.framework.config.PropertySource;
import com.framework.scanners.ComponentScanner;
import java.util.*;

public class MiniSpringContext implements ApplicationContext{
    private final ComponentScanner scanner;
    private final SimpleBeanFactory beanFactory;
    private final String basePackage;
    public MiniSpringContext(String basePackage) {
        PropertySource config = new PropertiesFileSource("config.properties");
        this.scanner = new ComponentScanner();
        this.basePackage = basePackage;
        this.beanFactory = new SimpleBeanFactory(config);

    }

    @Override
    public <T> T getBean(Class<T> beanClass) {
        return this.beanFactory.getBean(beanClass);
    }

    @Override
    public List<Class<?>> getRegisteredControllers(){
        return this.beanFactory.getRegisteredControllers();
    }

    @Override
    public void refresh() {

        List<Class<?>> scannedClasses = scanner.scan(basePackage);
        this.beanFactory.instantiate(scannedClasses);
    }

    @Override
    public <T> void registerSingleton(Class<T> type, T instance) {
        this.beanFactory.registerManualBean(type, instance);
    }

}
