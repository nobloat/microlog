package blubb;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class ExecutionPlan {

    @Param({"100"})
    public int iterations;
    //@Param({"1", "2", "10", "50"})
    //public int threads;

    @Setup(Level.Invocation)
    public void setup() {

    }
}
