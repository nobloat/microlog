package org.nobloat.log;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.tinylog.Logger;
import org.tinylog.ThreadContext;

import java.io.IOException;
import java.util.List;

@State(Scope.Benchmark)
public class Benchmark {

    public static final int TIME = 1;
    public static final int  ITERATIONS = 10;
    public static final int WARMUP_ITERATIONS = 3;

    public static StackTraceElement stack = new StackTraceElement(Benchmark.class.getCanonicalName(), "somemethod", "File.java", 1337);
    public static StringBuilder sb = new StringBuilder();

    @Setup(Level.Trial)
    public void setup() throws IOException {
        L.writers = List.of(new L.FileWriter("application.log"));
    }

    @Warmup(time = TIME, iterations = WARMUP_ITERATIONS)
    @Measurement(time = TIME, iterations = ITERATIONS)
    @org.openjdk.jmh.annotations.Benchmark
    @Fork(value = 1)
    @BenchmarkMode(Mode.Throughput)
    public void nobloatlogNoExceptions() {
        L.ctx().put("i", 10);
        L.info("Hello log world");
        L.ctx().put("user", 10);
    }

    @Warmup(time = TIME, iterations = WARMUP_ITERATIONS)
    @Measurement(time = TIME, iterations = ITERATIONS)
    @org.openjdk.jmh.annotations.Benchmark
    @Fork(value = 1)
    @BenchmarkMode(Mode.Throughput)
    public void nobloatlogExceptions() {
        L.ctx().put("i", 100);
        L.error("Runtime error", new CustomExcpetion("This is a runtime exception"));
        L.ctx().put("user", 100);
    }


    @Warmup(time = TIME, iterations = WARMUP_ITERATIONS)
    @Measurement(time = TIME, iterations = ITERATIONS)
    @org.openjdk.jmh.annotations.Benchmark
    @Fork(value = 1)
    @BenchmarkMode(Mode.Throughput)
    public void statementFormatterException() {
        L.DEFAULT_PATTERN.apply(sb, stack, L.Level.ERROR, "Some example message", new CustomExcpetion("this is an exception"));
        sb.setLength(0);
    }

    @Warmup(time = TIME, iterations = WARMUP_ITERATIONS)
    @Measurement(time = TIME, iterations = ITERATIONS)
    @org.openjdk.jmh.annotations.Benchmark
    @Fork(value = 1)
    @BenchmarkMode(Mode.Throughput)
    public void statementFormatterNoException() {

    }


    @Warmup(time = TIME, iterations = WARMUP_ITERATIONS)
    @Measurement(time = TIME, iterations = ITERATIONS)
    @org.openjdk.jmh.annotations.Benchmark
    @Fork(value = 1)
    @BenchmarkMode(Mode.Throughput)
    public void tinylogExceptions() {
        ThreadContext.put("i",100);
        Logger.error(new CustomExcpetion("This is a runtime exception"));
        ThreadContext.put("user", 100);
    }


    @Warmup(time = TIME, iterations = WARMUP_ITERATIONS)
    @Measurement(time = TIME, iterations = ITERATIONS)
    @org.openjdk.jmh.annotations.Benchmark
    @Fork(value = 1)
    @BenchmarkMode(Mode.Throughput)
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