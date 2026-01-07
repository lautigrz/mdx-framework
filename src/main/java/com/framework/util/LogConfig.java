package com.framework.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

public class LogConfig {
    public static void configure() {
        Logger rootLogger = Logger.getLogger("");

        Handler[] handlers = rootLogger.getHandlers();
        for (Handler handler : handlers) {
            rootLogger.removeHandler(handler);
        }

        Handler consoleHandler = new StreamHandler(System.out, new Formatter() {
            private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            @Override
            public String format(LogRecord record) {

                ZonedDateTime zdt = ZonedDateTime.ofInstant(
                        java.time.Instant.ofEpochMilli(record.getMillis()),
                        ZoneId.systemDefault());

                String throwable = "";
                if (record.getThrown() != null) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    pw.println();
                    record.getThrown().printStackTrace(pw);
                    pw.close();
                    throwable = sw.toString();
                }

                return String.format("[%s] [%-7s] %s - %s%s%n",
                        zdt.format(dateFormatter),
                        record.getLevel().getName(),
                        getSimpleClassName(record.getSourceClassName()),
                        formatMessage(record),
                        throwable
                );
            }
        }) {

            @Override
            public void publish(LogRecord record) {
                super.publish(record);
                flush();
            }
        };

        consoleHandler.setLevel(Level.INFO);
        rootLogger.addHandler(consoleHandler);
        rootLogger.setLevel(Level.INFO);
    }

    private static String getSimpleClassName(String fullClassName) {
        if (fullClassName == null) return "Unknown";
        int lastDot = fullClassName.lastIndexOf('.');
        return (lastDot == -1) ? fullClassName : fullClassName.substring(lastDot + 1);
    }
}
