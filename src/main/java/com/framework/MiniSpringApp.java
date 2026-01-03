package com.framework;

import com.framework.context.ApplicationContext;
import com.framework.web.DispatcherHandler;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

public class MiniSpringApp {
    public static void run(ApplicationContext applicationContext, int port) {

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

            server.createContext("/", new DispatcherHandler(applicationContext));

            server.setExecutor(null);
            server.start();
            System.out.println("🚀 Servidor web iniciado en http://localhost:" + port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
