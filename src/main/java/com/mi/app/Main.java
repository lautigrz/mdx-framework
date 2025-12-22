package com.mi.app;

import com.framework.context.ApplicationContext;
import com.framework.context.MiniSpringContext;
import com.mi.app.service.ReportService;


public class Main {
    public static void main(String[] args) {

        ApplicationContext context = new MiniSpringContext("com.mi.app");

        ReportService userService = context.getBean(ReportService.class);

        userService.generarReporte();
    }
}