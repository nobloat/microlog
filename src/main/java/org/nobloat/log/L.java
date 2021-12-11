package org.nobloat.log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class L {
    private static ThreadLocal<Map<String,Object>> context = new ThreadLocal<>();
    private static AtomicBoolean closed = new AtomicBoolean(false);

    public static ExceptionFormatter exceptionFormatter = new ExceptionFormatter();
    public static Timestamp timestamp = new Timestamp();
    public static SourceLocation sourceLocation = new SourceLocation();
    public static ThreadFormatter thread = new ThreadFormatter();
    public static ContextFormatter contextFormatter = new ContextFormatter();

    public static List<Writer> writers = List.of(new ConsoleWriter());
    public static Level minLevel = Level.DEBUG;
    public static final Pattern DEFAULT_PATTERN = (l,m,e) -> String.format("%s %s %s [%s]%s: %s%s", timestamp.ts(), l, sourceLocation.format(), thread.format(), contextFormatter.format(), m, exceptionFormatter.format(e));
    public static Pattern pattern = DEFAULT_PATTERN;

    private L() {}

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(L::close));
    }

    public interface Writer {
        void write(Level l, String s);
        void close();
    }

    public static class ConsoleWriter implements Writer {
        @Override
        public void write(Level l, String s) {
            switch (l) {
                case ERROR -> System.err.println("\u001B[31m"+s);
                case WARNING -> System.out.println("\u001B[33m"+s);
                case INFO -> System.out.println("\u001B[32m"+s);
                case DEBUG, TRACE -> System.out.println(s);
            }
            if (l == Level.ERROR) {
                System.err.println(s+"\u001b[0m");
            } else {
                System.out.println(s+"\u001b[0m");
            }
        }
        @Override
        public void close() {
            System.out.flush();
            System.err.flush();
        }
    }

    public static class FileWriter implements Writer {
        private final BufferedWriter writer;

        public FileWriter(String path) throws IOException {
            writer = new BufferedWriter(new java.io.FileWriter(path));
        }
        @Override
        public void write(Level l, String s) {
            try {
                writer.append(s).append('\n');
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void close() {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FunctionalInterface
    public interface Pattern {
        String apply(Level level, String message, Throwable e);
    }

    public static class ContextFormatter {
        public String format() {
            if (context.get() == null)
                return "";
            var keyValues = context.get().entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining(","));
            return "<" + keyValues + ">";
        }
    }

    public static class Timestamp {
        public DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        public String ts() {
            return formatter.format(LocalDateTime.now());
        }
    }

    public static class SourceLocation {
        public String format() {
            var stackElement = Thread.currentThread().getStackTrace()[4];
            return stackElement.getClassName() + "." + stackElement.getMethodName() + "(" + stackElement.getFileName() + ":" + stackElement.getLineNumber() + ")";
        }
    }

    public static class ThreadFormatter {
        public String format() {
            return Thread.currentThread().getName();
        }
    }

    public static class ExceptionFormatter {
        public String format(Throwable e) {
            if (e != null) {
                var sb = new StringBuilder();
                sb.append(e.getMessage() + " " + e.getClass().getCanonicalName() +  ":\n");
                for (var se : e.getStackTrace()) {
                    sb.append("\t" + se.toString());
                }
                return " " + sb;
            }
            return "";
        }
    }

    public static void trace(String message) {
        if (Level.TRACE.value < minLevel.value) return;
        write(Level.TRACE, pattern.apply(Level.TRACE, message, null));
    }

    public static void debug(String message) {
        if (Level.DEBUG.value < minLevel.value) return;
        write(Level.DEBUG, pattern.apply(Level.DEBUG, message, null));

    }
    public static void info(String message) {
        if (Level.INFO.value < minLevel.value) return;
        write(Level.INFO, pattern.apply(Level.INFO, message, null));
    }

    public static void warn(String message) {
        if (Level.WARNING.value < minLevel.value) return;
        write(Level.WARNING, pattern.apply(Level.WARNING, message, null));
    }

    public static void error(String message, Throwable e) {
        if (Level.ERROR.value < minLevel.value) return;
        write(Level.ERROR, pattern.apply(Level.ERROR, message, e));
    }

    private static void write(Level l, String message) {
        writers.stream().forEach(w -> w.write(l, message));
    }

    public static Map<String,Object> ctx() {
        if (context.get() == null) {
            context.set(new HashMap<>());
        }
        return context.get();
    }

    public static void close() {
        if (!closed.get()) {
            closed.set(true);
            writers.forEach(Writer::close);
        }
    }

    public enum Level {
        INFO(1), WARNING(2), ERROR(3), TRACE(-2), DEBUG(-1);

        int value;

        Level(int value) {
            this.value = value;
        }
    }

}
