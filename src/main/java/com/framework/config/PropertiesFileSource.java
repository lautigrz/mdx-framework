package com.framework.config;

import java.io.FileInputStream;
import java.util.Properties;

public class PropertiesFileSource implements PropertySource {

    private final Properties properties;
    public PropertiesFileSource(String filename) {
        this.properties = new Properties();
        try{
            properties.load(new FileInputStream("src/main/resources/"+filename));
        }catch (Exception e){
            throw new RuntimeException("No se pudo cargar el archivo de propiedades: " + filename);
        }
    }


    @Override
    public String getProperty(String name) {
        return properties.getProperty(name);
    }
}
