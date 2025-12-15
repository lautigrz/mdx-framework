package com.framework.scanners;

import com.framework.annotations.Component;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ComponentScanner {

    public List<Class<?>> scan (String packageRoute) {
        List<Class<?>> clases = new ArrayList<>();

        scanRecursively(packageRoute, clases);

        return clases;
    }


    private URL resolvePath(String packageRoute){
        packageRoute = packageRoute.replace(".", "/");
        return ClassLoader.getSystemClassLoader().getResource(packageRoute);

      }

      private void scanRecursively(String packageRoute, List<Class<?>> classes){
        URL url = resolvePath(packageRoute);

        if (url == null) return;

        File file = new File(url.getFile());
        File[] files = file.listFiles();

        if(files == null) return;

        for (File f : files){
                  if (f.isDirectory()) {
                      scanRecursively(packageRoute + "." + f.getName(),classes);
                  } else {
                      processFile(f,packageRoute,classes);
                  }
              }
      }

    private void processFile(File file, String packageName, List<Class<?>> classes) {

        if (!file.getName().endsWith(".class")) {
            return;
        }

        String className = buildClassName(packageName, file);

        try {

            Class<?> clase = Class.forName(className);

            if (isComponent(clase)) {
                classes.add(clase);
            }

        } catch (ClassNotFoundException | NoClassDefFoundError e) {

            System.err.println("Omitiendo clase corrupta o incompleta: " + className);
        }
    }

      private String buildClassName(String packageName, File file){
          String simpleClassName = file.getName().replace(".class", "");

          String normalizedPackage = packageName.replace("/", ".").replace("\\", ".");

          return normalizedPackage + "." + simpleClassName;
      }


      private boolean isComponent(Class<?> classes){
          return classes.isAnnotationPresent(Component.class);
      }
}