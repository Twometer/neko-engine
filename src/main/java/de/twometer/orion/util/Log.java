package de.twometer.orion.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

    private static final DateFormat format = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

    private static void write(String prefix, String message) {
        var caller = Thread.currentThread().getStackTrace()[3];
        var date = format.format(new Date());
        System.out.printf("[%s] [%s] [%s/%s] %s%n", prefix, date, caller.getClassName(), caller.getMethodName(), message);
    }

    public static void d(String message) {
        write("DEBUG", message);
    }

    public static void i(String message) {
        write("INFO", message);
    }

    public static void w(String message) {
        write("WARN", message);
    }

    public static void e(String message) {
        write("ERROR", message);
    }

    public static void e(String message, Throwable t) {
        write("ERROR", message + ": " + t.toString());
    }

}
