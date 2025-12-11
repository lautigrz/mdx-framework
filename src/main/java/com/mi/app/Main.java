package com.mi.app;

import com.framework.config.PropertiesFileSource;
import com.framework.config.PropertySource;
import com.framework.context.ApplicationContext;
import com.framework.context.MiniSpringContext;
import com.mi.app.repository.UserRepositoryImpl;
import com.mi.app.service.IUserService;
import com.mi.app.service.UserServiceImpl;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Class<?>> classes = List.of(UserServiceImpl.class, UserRepositoryImpl.class);

        PropertySource fuente = new PropertiesFileSource("config.properties");
        ApplicationContext context = new MiniSpringContext(fuente, classes);

        IUserService userService = context.getBean(UserServiceImpl.class);

       System.out.println(userService.getUserInfo());
    }
}