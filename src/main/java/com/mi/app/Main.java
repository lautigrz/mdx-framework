package com.mi.app;

import com.framework.MiniSpringApp;
import com.framework.context.ApplicationContext;
import com.framework.context.MiniSpringContext;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- Arrancando Contexto ---");

        ApplicationContext context = new MiniSpringContext("com.mi.app");


        System.out.println("--- Arrancando Servidor Web ---");
        MiniSpringApp.run(context, 8080);
    }
}