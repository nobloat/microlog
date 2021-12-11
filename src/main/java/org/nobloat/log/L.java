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
    public static final Pattern DEFAULT_PATTERN = (l, m, e) -> {

        var stackElement = getCallerStackTraceElement(3);

        var sb = new StringBuilder();
        sb.append(timestamp.ts()).append(' ');
        sourceLocation.format(stackElement, sb).append(' ');
        sb.append('[').append(thread.format()).append("]");
        sb.append(contextFormatter.format()).append(":");
        sb.append(m);
        exceptionFormatter.format(sb,e);
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
        @Override
        public void write(Level l, CharSequence s) {
            switch (l) {
                case ERROR -> System.err.print("\u001B[31m");
                case WARNING -> System.out.print("\u001B[33m");
                case INFO -> System.out.print("\u001B[32m");
                default -> System.out.print("\u001b[0m");
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
        CharSequence apply(Level level, String message, Throwable e);
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
        public StringBuilder format(StackTraceElement stackElement, StringBuilder sb) {
            sb.append(stackElement.getClassName()).append('.').append(stackElement.getMethodName()).append('(').append(stackElement.getFileName()).append(':').append(stackElement.getLineNumber()).append(')');
            return sb;
        }
    }

    public static class ThreadFormatter {
        public String format() {
            return Thread.currentThread().getName();
        }
    }

    public static class ExceptionFormatter {
        public void format(StringBuilder sb, Throwable e) {
            if (e != null) {
                sb.append(e.getMessage()).append(' ').append(e.getClass().getCanonicalName()).append(":\n");
                var st = e.getStackTrace();
                //sb.append(String.valueOf(st.length));
                for (var se : e.getStackTrace()) {
                    sb.append("\t" + se.toString()).append('\n');
                }
            }
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

    private static void write(Level l, CharSequence cs) {
        writers.stream().forEach(w -> w.write(l, cs));
    }

    public static Map<String,Object> ctx() {
        if (context.get() == null) {
            context.set(new HashMap<>());
        }
        return context.get();
    }

    public static void close() {
        System.out.println("Trying to close");
        if (!closed.get()) {
            closed.set(true);
            writers.forEach(Writer::close);
            System.out.println("Closed");
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
