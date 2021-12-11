import java.io.IOException;
import java.util.ArrayList;

import org.nobloat.log.L;
import org.tinylog.Logger;
import org.tinylog.ThreadContext;
import org.tinylog.provider.ProviderRegistry;

import java.util.List;

public class Main {


    public static void nobloatLog(int threadCount, int messagesPerThread) throws IOException {
        L.writers = List.of(new L.ConsoleWriter(true));
        var threads = new ArrayList<Thread>();
        for (int i = 0; i < threadCount; i++) {
            final int param = i;
            var t = new Thread(() -> {
                L.ctx().put("i", param);
                for(int j = 0; j < messagesPerThread; j++) {
                    L.info("Hello log world");
                    L.error("Runtime error", new RuntimeException("This is a runtime exception"));
                    L.ctx().put("user", j);
                    L.warn("This is a warning");
                    L.info("This is a info");
                    L.debug("This is a debug");
                    L.trace("This is a trace");
                }
            });
            threads.add(t);
            t.start();
        }

        threads.stream().forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                L.error("", e);
            }
        });

        L.close();
    }

    public static void tinyLog(int threadCount, int messagesPerThread) throws InterruptedException {
        var threads = new ArrayList<Thread>();
        for (int i = 0; i < threadCount; i++) {
            final int param = i;
            var t = new Thread(() -> {
                ThreadContext.put("i", param);
                for(int j = 0; j < messagesPerThread; j++) {
                    Logger.info("Hello log world");
                    Logger.error("Runtime error", new RuntimeException("This is a runtime exception"));
                    ThreadContext.put("user", j);
                    Logger.warn("This is a warning");
                    Logger.info("This is a info");
                    Logger.debug("This is a debug");
                    Logger.trace("This is a trace");
                }
            });
            threads.add(t);
            t.start();
        }

        threads.stream().forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                L.error("", e);
            }
        });

        ProviderRegistry.getLoggingProvider().shutdown();

    }

    public static class CustomExcpetion extends RuntimeException {
        public CustomExcpetion(String s) {
            super(s);
        }

    }

    public static void f2() {
        f1();
    }

    public static void f1() {
        throw  new CustomExcpetion("ffff");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        /*{
            long start = System.currentTimeMillis();
            //tinyLog(2, 10000);
            nobloatLog(1, 1000);
            long finish = System.currentTimeMillis();
            long timeElapsed = finish - start;
            Thread.sleep(500);
            System.out.println("Elapesed: " + timeElapsed);
        }*/


        L.writers = List.of(new L.ConsoleWriter(false), new L.FileWriter("application.log"));

        try {
            L.info("Calling f2");

            throw new RuntimeException("fff");

            //f2();
        } catch (Exception e) {
            L.error("", new RuntimeException("bluuuub"));
        }

    }
}
