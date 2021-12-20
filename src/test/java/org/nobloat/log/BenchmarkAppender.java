package org.nobloat.log;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Warmup;

public class BenchmarkAppender {

    public static final int TIME = 1;
    public static final int  ITERATIONS = 10;
    public static final int WARMUP_ITERATIONS = 3;

    public static StackTraceElement stack = new StackTraceElement(Benchmark.class.getCanonicalName(), "somemethod", "File.java", 1337);
    public static StringBuilder sb = new StringBuilder();

    @Warmup(time = TIME, iterations = WARMUP_ITERATIONS)
    @Measurement(time = TIME, iterations = ITERATIONS)
    @org.openjdk.jmh.annotations.Benchmark
    @Fork(value = 1)
    @BenchmarkMode(Mode.Throughput)
    public void statementFormatterException() {
        L.DEFAULT_PATTERN.apply(sb, stack, L.Level.ERROR, "Some example message", new Benchmark.CustomExcpetion("this is an exception"));
        sb.setLength(0);
    }
}
