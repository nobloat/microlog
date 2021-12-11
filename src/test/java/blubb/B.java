package blubb;

import org.nobloat.log.L;
import org.openjdk.jmh.annotations.Benchmark;
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
public class B {

    public static final int TIME = 1;
    public static final int  ITERATIONS = 10;
    public static final int WARMUP_ITERATIONS = 3;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        L.writers = List.of(new L.FileWriter("application.log"));
    }

    @Warmup(time = TIME, iterations = WARMUP_ITERATIONS)
    @Measurement(time = TIME, iterations = ITERATIONS)
    @Benchmark
    @Fork(value = 1)
    @BenchmarkMode(Mode.Throughput)
    public void nobloatlogNoExceptions(ExecutionPlan p) {
        L.ctx().put("i", p.iterations);
        L.info("Hello log world");
        L.trace("Trace should be ignored");
        L.ctx().put("user", p.iterations);
    }

    @Warmup(time = TIME, iterations = WARMUP_ITERATIONS)
    @Measurement(time = TIME, iterations = ITERATIONS)
    @Benchmark
    @Fork(value = 1)
    @BenchmarkMode(Mode.Throughput)
    public void nobloatlogExceptions(ExecutionPlan p) {
        ThreadContext.put("i", p.iterations);
        L.error("Runtime error", new CustomExcpetion("This is a runtime exception"));
        L.ctx().put("user", p.iterations);
    }



    @Warmup(time = TIME, iterations = WARMUP_ITERATIONS)
    @Measurement(time = TIME, iterations = ITERATIONS)
    @Benchmark
    @Fork(value = 1)
    @BenchmarkMode(Mode.Throughput)
    public void tinylogExceptions(ExecutionPlan p) {
        ThreadContext.put("i", p.iterations);
        Logger.error(new CustomExcpetion("This is a runtime exception"));
        ThreadContext.put("user", p.iterations);
    }


    @Warmup(time = TIME, iterations = WARMUP_ITERATIONS)
    @Measurement(time = TIME, iterations = ITERATIONS)
    @Benchmark
    @Fork(value = 1)
    @BenchmarkMode(Mode.Throughput)
    public void tinylogNoExceptions(ExecutionPlan p) {
        ThreadContext.put("i", p.iterations);
        Logger.info("Hello log world");
        L.trace("Trace should be ignored");
        ThreadContext.put("user", p.iterations);
    }

    public static class CustomExcpetion extends RuntimeException {
        public CustomExcpetion(String s) {
            super(s);
        }
    }

}
