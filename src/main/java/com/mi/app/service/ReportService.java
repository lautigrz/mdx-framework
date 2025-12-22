package com.mi.app.service;

import com.framework.annotations.Service;
import com.framework.context.Provider;

@Service
public class ReportService {

    private final Provider<DatabaseService> databaseServiceProvider;

    public ReportService(Provider<DatabaseService> databaseServiceProvider) {
        this.databaseServiceProvider = databaseServiceProvider;
        System.out.println(">>> Constructor ReportService: No he creado la DB todavía.");

    }

    public void generarReporte() {
        System.out.println("--- Generando reporte ---");

        // ¡AQUÍ! Recién ahora se llama a getBean() internamente
        // Si DatabaseService no existía, se crea en este milisegundo.
        DatabaseService db = databaseServiceProvider.get();

        System.out.println("Usando DB: " + db);
    }


}
