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
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.tinylog.Logger;
import org.tinylog.ThreadContext;
import org.tinylog.provider.ProviderRegistry;

import java.io.IOException;
import java.util.List;

@State(Scope.Benchmark)
public class B {

    @Setup(Level.Invocation)
    public void setup() throws IOException {
        L.writers = List.of(new L.FileWriter("application.log"));
    }

    @TearDown
    public void teardown() throws InterruptedException, IOException {
        L.close();
        ProviderRegistry.getLoggingProvider().shutdown();
        //Files.deleteIfExists(Path.of("application.log"));
        //Files.deleteIfExists(Path.of("tinylog.log"));
    }

    @Warmup(time = 3, iterations = 2)
//    @Fork(value = 1, warmups = 3)
    @Measurement(time = 3, iterations = 3)
    @Benchmark
    @Fork(value = 1)
    @BenchmarkMode(Mode.Throughput)
    public void nobloatlog(ExecutionPlan p) {
        L.ctx().put("i", p.iterations);
        //for (int i =0; i < p.iterations; i++) {
            L.info("Hello log world");
            L.error("Runtime error", new RuntimeException("This is a runtime exception"));
            L.ctx().put("user", p.iterations);
            L.warn("This is a warning");
            L.info("This is a info");
            L.debug("This is a debug");
            L.trace("This is a trace");
        //}
    }

    @Warmup(time = 3, iterations = 2)
    @Measurement(time = 3, iterations = 3)
  //  @Fork(value = 1, warmups = 3)
    @Benchmark
    @Fork(value = 1)
    @BenchmarkMode(Mode.Throughput)
    public void tinylog(ExecutionPlan p) {
        ThreadContext.put("i", p.iterations);
        //for (int i =0; i < p.iterations; i++) {
            Logger.info("Hello log world");
            Logger.error("Runtime error", new RuntimeException("This is a runtime exception"));
            ThreadContext.put("user", p.iterations);
            Logger.warn("This is a warning");
            Logger.info("This is a info");
            Logger.debug("This is a debug");
            Logger.trace("This is a trace");
        //}
    }


}
