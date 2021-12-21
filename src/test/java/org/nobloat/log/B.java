package org.nobloat.log;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.tinylog.Logger;
import org.tinylog.ThreadContext;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;


@State(Scope.Benchmark)
@Warmup(time = 1, iterations = 3)
@Measurement(time = 1, iterations = 10)
@Fork(value = 1)
@Threads(value = 1)
@BenchmarkMode(Mode.Throughput)
public class B {

    public static StackTraceElement stack = new StackTraceElement(B.class.getCanonicalName(), "somemethod", "File.java", 1337);
    public static StringBuilder sb = new StringBuilder();

    @Setup(Level.Trial)
    public void setup() throws IOException {
        L.writers = List.of(new RollingFileWriter(Path.of(""), "test_*.log", () -> "0", 10));
    }

    @Benchmark
    public void nobloatlogNoExceptions() {
        L.ctx().put("i", 10);
        L.info("Hello log world");
        L.ctx().put("user", 10);
    }

    @Benchmark
    public void nobloatlogExceptions() {
        L.ctx().put("i", 100);
        L.error("Runtime error", new CustomExcpetion("This is a runtime exception"));
        L.ctx().put("user", 100);
    }

    @Benchmark
    public void statementFormatterException() {
        L.ctx().put("i", 100);
        L.ctx().put("user", 100);
        L.DEFAULT_PATTERN.apply(sb, stack, L.Level.ERROR, "Some example message", new CustomExcpetion("this is an exception"));
        sb.setLength(0);
    }

    @Benchmark
    public void statementFormatterNoException() {
        L.ctx().put("i", 100);
        L.ctx().put("user", 100);
        L.DEFAULT_PATTERN.apply(sb, stack, L.Level.ERROR, "Some example message", null);
        sb.setLength(0);
    }

    @Benchmark
    public void tinylogExceptions() {
        ThreadContext.put("i",100);
        Logger.error(new CustomExcpetion("This is a runtime exception"));
        ThreadContext.put("user", 100);
    }

    @Benchmark
    public void tinylogNoExceptions() {
        ThreadContext.put("i", 100);
        Logger.info("Hello log world");
        ThreadContext.put("user", 100);
    }

    public static class CustomExcpetion extends RuntimeException {
        public CustomExcpetion(String s) {
            super(s);
        }
    }

}
