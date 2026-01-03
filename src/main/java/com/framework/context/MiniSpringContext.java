package com.framework.context;

import com.framework.config.PropertiesFileSource;
import com.framework.config.PropertySource;
import com.framework.scanners.ComponentScanner;
import java.util.*;

public class MiniSpringContext implements ApplicationContext{
    private final PropertySource config;
    private final ComponentScanner scanner;
    private final SimpleBeanFactory beanFactory;
    public MiniSpringContext(String basePackage) {
        this.config = new PropertiesFileSource("config.properties");
        this.scanner = new ComponentScanner();
        List<Class<?>> classes = scanner.scan(basePackage);
        this.beanFactory = new SimpleBeanFactory(classes,config);

    }

    @Override
    public <T> T getBean(Class<T> beanClass) {
        return this.beanFactory.getBean(beanClass);
    }

    @Override
    public List<Class<?>> getRegisteredControllers(){
        return this.beanFactory.getRegisteredControllers();
    }
}
