package com.mi.app;

import com.framework.config.PropertiesFileSource;
import com.framework.config.PropertySource;
import com.framework.context.ApplicationContext;
import com.framework.context.MiniSpringContext;
import com.mi.app.service.IUserService;
import com.mi.app.service.UserServiceImpl;

public class Main {
    public static void main(String[] args) {

        PropertySource fuente = new PropertiesFileSource("config.properties");

        ApplicationContext context = new MiniSpringContext(fuente, "com.mi.app");

        IUserService userService = context.getBean(UserServiceImpl.class);

        System.out.println(userService.getUserInfo());

    }
}