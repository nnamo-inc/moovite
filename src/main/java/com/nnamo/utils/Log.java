package com.nnamo.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

public final class Log {
    private static final Logger ROOT = Logger.getLogger("moovite");

    static {
        ROOT.setUseParentHandlers(false);
        boolean hasConsole = false;
        for (Handler h : ROOT.getHandlers()) {
            if (h instanceof ConsoleHandler) {
                hasConsole = true;
                break;
            }
        }
        if (!hasConsole) {
            ConsoleHandler ch = new ConsoleHandler();
            ch.setLevel(Level.ALL);
            ch.setFormatter(new CustomFormatter());
            ROOT.addHandler(ch);
        }
        ROOT.setLevel(Level.INFO);
    }

    private Log() {
    }

    public static void setLevel(Level level) {
        ROOT.setLevel(level);
    }

    public static void trace(String msg) {
        ROOT.finest(msg);
    }

    public static void debug(String msg) {
        ROOT.fine(msg);
    }

    public static void info(String msg) {
        ROOT.info(msg);
    }

    public static void warn(String msg) {
        ROOT.warning(msg);
    }

    public static void error(String msg) {
        ROOT.severe(msg);
    }

    public static void error(String msg, Throwable t) {
        ROOT.log(Level.SEVERE, msg, t);
    }

    /**
     * Custom log formatter to format log messages with timestamp, level, class name, and message.
     * 
     * @author AI
     */
    private static class CustomFormatter extends Formatter {
        private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

        @Override
        public String format(LogRecord record) {
            String timestamp = LocalDateTime.now().format(TIME_FORMAT);
            String level = formatLevel(record.getLevel());
            String className = getSimpleClassName(record.getSourceClassName());

            return String.format("[%s] %s %s - %s%n",
                    timestamp, level, className, record.getMessage());
        }

        private String formatLevel(Level level) {
            if (level == Level.SEVERE) return "ERROR";
            if (level == Level.WARNING) return "WARN ";
            if (level == Level.INFO) return "INFO ";
            if (level == Level.FINE) return "DEBUG";
            if (level == Level.FINEST) return "TRACE";
            return level.toString();
        }

        private String getSimpleClassName(String className) {
            if (className == null) return "Unknown";
            int lastDot = className.lastIndexOf('.');
            return lastDot >= 0 ? className.substring(lastDot + 1) : className;
        }
    }
}
