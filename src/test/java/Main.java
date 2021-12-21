import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import org.nobloat.log.L;
import org.nobloat.log.RollingFileWriter;
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

    public static class Person {
        String fistname;
        String lastname;

        public Person(String fistname, String lastname) {
            this.fistname = fistname;
            this.lastname = lastname;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "fistname='" + fistname + '\'' +
                    ", lastname='" + lastname + '\'' +
                    '}';
        }
    }

    public static void f2() {
        f1();
    }

    public static void f1() {
        throw  new CustomExcpetion("ffff");
    }

    public static void main(String[] args) throws IOException {

        L.writers = List.of(new L.ConsoleWriter(false), new RollingFileWriter(Path.of(""), "mylog_*.log", RollingFileWriter.WEEKLY, 10));

        try {

            L.ctx().put("key1", "val1");
            L.ctx().put("key2", "val2");

            L.info("Calling f2");

            L.warn("This is a person warning %s", new Person("hugo", "fugo"));

            throw new RuntimeException("fff");

            //f2();
        } catch (Exception e) {
            L.error("", new RuntimeException("bluuuub"));
        }

    }
}
