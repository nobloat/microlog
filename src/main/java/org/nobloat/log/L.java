package org.nobloat.log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class L {

    public static ExceptionFormatter exceptionFormatter = new ExceptionFormatter();
    public static DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public static List<Writer> writers = List.of(new ConsoleWriter(true));
    public static volatile Level minLevel = Level.DEBUG;

    public enum Level {TRACE, DEBUG, INFO, WARNING, ERROR}

    private static ThreadLocal<StringBuilder> stringBuilder = ThreadLocal.withInitial(StringBuilder::new);

    public static final Pattern DEFAULT_PATTERN = (sb, location, l, m, e) -> {
        sb.append(timestampFormatter.format(LocalDateTime.now())).append(' ');
        sb.append(l).append(' ');
        sb.append(location.getClassName())
                .append('.').append(location.getMethodName()).append('(').append(location.getFileName())
                .append(':').append(location.getLineNumber()).append(')').append(' ');

        sb.append('[').append(Thread.currentThread().getName()).append("]: ");
        sb.append(m);
        if (e != null) {
            exceptionFormatter.format(sb,e);
        } else {
            sb.append('\n');
        }
        return sb;
    };

    public static Pattern pattern = DEFAULT_PATTERN;

    private L() {}

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(L::close));
    }

    public interface Writer {
        void write(Level l, CharSequence s) throws IOException;
        void close() throws IOException;
    }

    public static class ConsoleWriter implements Writer {
        private boolean colors;
        public ConsoleWriter(boolean withColors) {
            this.colors = withColors;
        }
        @Override
        public void write(Level l, CharSequence s) {
            if (colors) {
                switch (l) {
                    case ERROR -> System.err.print("\u001B[31m");
                    case WARNING -> System.out.print("\u001B[33m");
                    case INFO -> System.out.print("\u001B[32m");
                    default -> System.out.print("\u001b[0m");
                }
            }
            if (l == Level.ERROR) {
                System.err.println(s);
            } else {
                System.out.println(s);
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
            writer = new BufferedWriter(new java.io.FileWriter(path,true));
        }
        @Override
        public void write(Level l, CharSequence s) throws IOException {
            writer.append(s).append('\n');
        }
        @Override
        public void close() throws IOException {
            writer.close();
        }
    }

    @FunctionalInterface
    public interface Pattern {
        CharSequence apply(StringBuilder sb, StackTraceElement stackTraceElement, Level level, String message, Throwable e);
    }

    public static class ExceptionFormatter {
        public void format(StringBuilder sb, Throwable e) {
            sb.append(e.getClass().getCanonicalName()).append(' ').append(e.getMessage()).append(" at\n");
            for (var se : e.getStackTrace()) {
                sb.append('\t').append(se.toString()).append('\n');
            }
        }
    }

    public static void trace(String message, Object... params) {
        if (Level.TRACE.ordinal() < minLevel.ordinal()) return;
        write(Level.TRACE, pattern.apply(stringBuilder.get(), getCallerStackTraceElement(), Level.DEBUG, fmt(message, params), null));
    }

    public static void debug(String message, Object... params) {
        if (Level.DEBUG.ordinal() < minLevel.ordinal()) return;
        write(Level.DEBUG, pattern.apply(stringBuilder.get(), getCallerStackTraceElement(), Level.DEBUG, fmt(message, params), null));
    }

    public static void info(String message, Object... params) {
        if (Level.INFO.ordinal() < minLevel.ordinal()) return;
        write(Level.INFO, pattern.apply(stringBuilder.get(), getCallerStackTraceElement(), Level.INFO, fmt(message, params), null));
    }

    public static void warn(String message, Object... params) {
        if (Level.WARNING.ordinal() < minLevel.ordinal()) return;
        write(Level.WARNING, pattern.apply(stringBuilder.get(), getCallerStackTraceElement(), Level.WARNING, fmt(message, params), null));
    }

    public static void error(String message, Throwable e, Object... params) {
        if (Level.ERROR.ordinal() < minLevel.ordinal()) return;
        write(Level.ERROR, pattern.apply(stringBuilder.get(), getCallerStackTraceElement(), Level.ERROR, fmt(message, params), e));
    }

    private static String fmt(String message, Object[] params) {
        if (params.length == 0)
            return message;
        return String.format(message, params);
    }

    private static void write(Level l, CharSequence cs) {
        writers.forEach(w -> {
            try {
                w.write(l, cs);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static Map<String,Object> ctx() {
        if (context == null) {
            context = ThreadLocal.withInitial(HashMap::new);
        }
        return context.get();
    }

    public static void close() {
        writers.forEach(w -> {
            try {
                w.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static StackTraceElement getCallerStackTraceElement() {
        StackWalker.StackFrame frame = StackWalker.getInstance().walk(s -> s.skip(2).findFirst().orElse(null));
        return frame == null ? null : frame.toStackTraceElement();
    }

    private static ThreadLocal<Map<String,Object>> context = null;
}
