package org.nobloat.log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class L {
    private static final ThreadLocal<Map<String,Object>> context = new ThreadLocal<>();
    private static final AtomicBoolean closed = new AtomicBoolean(false);
    private static final int STACK_DEPTH = 2;

    public static ExceptionFormatter exceptionFormatter = new ExceptionFormatter();
    public static DateTimeFormatter timestampFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static List<Writer> writers = List.of(new ConsoleWriter(true));
    public static Level minLevel = Level.DEBUG;

    // String.format("<timestamp> <location> <level>: %s%s")
    public static final Pattern DEFAULT_PATTERN = (location, l, m, e) -> {
        var sb = new StringBuilder();
        sb.append(timestampFormatter.format(LocalDateTime.now())).append(' ');
        sb.append(l).append(' ');
        sb.append(location.getClassName())
                .append('.').append(location.getMethodName()).append('(').append(location.getFileName())
                .append(':').append(location.getLineNumber()).append(')').append(' ');

        sb.append('[').append(Thread.currentThread().getName()).append("]: ");
        sb.append(m);
        if (e != null) {
            exceptionFormatter.format(sb,e);
        }
        return sb;
    };

    public static Pattern pattern = DEFAULT_PATTERN;

    private L() {}

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(L::close));
    }

    public interface Writer {
        void write(Level l, CharSequence s);
        void close();
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
        public void write(Level l, CharSequence s) {
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
        CharSequence apply(StackTraceElement stackTraceElement, Level level, String message, Throwable e);
    }

    public static class ExceptionFormatter {
        public void format(StringBuilder sb, Throwable e) {
            sb.append(e.getMessage()).append(' ').append(e.getClass().getCanonicalName()).append(":\n");
            for (var se : e.getStackTrace()) {
                sb.append('\t').append(se.toString()).append('\n');
            }
        }
    }

    public static void trace(String message) {
        if (Level.TRACE.value < minLevel.value) return;
        write(Level.TRACE, pattern.apply(getCallerStackTraceElement(STACK_DEPTH), Level.TRACE, message, null));
    }

    public static void debug(String message) {
        if (Level.DEBUG.value < minLevel.value) return;
        write(Level.DEBUG, pattern.apply(getCallerStackTraceElement(STACK_DEPTH), Level.DEBUG, message, null));

    }
    public static void info(String message) {
        if (Level.INFO.value < minLevel.value) return;
        write(Level.INFO, pattern.apply(getCallerStackTraceElement(STACK_DEPTH), Level.INFO, message, null));
    }

    public static void warn(String message) {
        if (Level.WARNING.value < minLevel.value) return;
        write(Level.WARNING, pattern.apply(getCallerStackTraceElement(STACK_DEPTH), Level.WARNING, message, null));
    }

    public static void error(String message, Throwable e) {
        if (Level.ERROR.value < minLevel.value) return;
        write(Level.ERROR, pattern.apply(getCallerStackTraceElement(STACK_DEPTH), Level.ERROR, message, e));
    }

    private static void write(Level l, CharSequence cs) {
        writers.forEach(w -> w.write(l, cs));
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

    private static StackTraceElement getCallerStackTraceElement(final int depth) {
        StackWalker.StackFrame frame = StackWalker.getInstance().walk(s -> s.skip(depth).findFirst().orElse(null));
        return frame == null ? null : frame.toStackTraceElement();
    }

    public enum Level {
        INFO(1), WARNING(2), ERROR(3), TRACE(-2), DEBUG(-1);
        int value;
        Level(int value) {
            this.value = value;
        }
    }
}
