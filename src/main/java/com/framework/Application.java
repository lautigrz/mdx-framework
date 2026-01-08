package com.framework;

import com.framework.context.ApplicationContext;
import com.framework.context.MiniSpringContext;
import com.framework.util.LogConfig;
import com.framework.util.SimpleSerialization;
import com.framework.web.DispatcherHandler;
import com.framework.web.response.DefaultResponseConverter;
import com.framework.web.response.HttpResponseWriter;
import com.framework.web.response.ResponseConverter;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class Application {

    private static final Logger logger = Logger.getLogger(Application.class.getName());

    public static void run(Class<?> primarySource, String... args) {
        LogConfig.configure();

        long startTime = System.currentTimeMillis();
        String basePackage = primarySource.getPackageName();
        ApplicationContext context = new MiniSpringContext(basePackage);

        logger.info("Iniciando MiniFramework para " + primarySource.getSimpleName() + "...");
        try {

            startWebServer(context);

        } catch (Exception e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        logger.info("Aplicación iniciada en " + (endTime - startTime) / 1000.0 + " segundos.");
    }

    private static void startWebServer(ApplicationContext context) throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", initializeWebDispatcher(context));

        server.setExecutor(null);
        server.start();
        logger.info("Servidor iniciado en " + "http://localhost:8080");
    }

    private static DispatcherHandler initializeWebDispatcher(ApplicationContext context) {
        SimpleSerialization simpleSerialization = new SimpleSerialization();
        HttpResponseWriter responseWriter = new HttpResponseWriter();
        ResponseConverter converter = new DefaultResponseConverter(simpleSerialization);

        return new DispatcherHandler(context, responseWriter, converter);
    }




}
