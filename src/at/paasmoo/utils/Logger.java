/*
 * JAVADRAW
 * paasmoo 2022 (GitHub)
 */
package at.paasmoo.utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.SimpleFormatter;

public class Logger {
    public static java.util.logging.Logger jlogger = java.util.logging.Logger.getLogger("at.paasmoo");

    public Logger(String filename) {
        super();
        try {
            if (filename.endsWith("txt")) {
                Handler handler = new FileHandler(filename);
                handler.setFormatter(new SimpleFormatter());
                jlogger.addHandler(handler);
            } else {
                jlogger.addHandler(new FileHandler(filename));
            }
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
        jlogger.info("Created log file \"" + filename + "\"");
    }

    public static String stackTraceToString(Throwable e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
