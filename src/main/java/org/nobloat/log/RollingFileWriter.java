package org.nobloat.log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class RollingFileWriter implements L.Writer {

    LocalDateTime created;
    Duration duration;
    BufferedWriter currentWriter;
    Path directory;
    int backups;
    String pattern;
    Supplier<String> filename;

    public RollingFileWriter(Duration duration, Path directory, String pattern, Supplier<String> filename, int backups) throws IOException {
        if (!pattern.contains("*")) {
            throw new IllegalArgumentException("Pattern must contain exactly one asterisk (*)");
        }
        this.duration = duration;
        this.filename = filename;
        this.pattern = pattern;
        this.directory = directory;
        this.created = LocalDateTime.now();
        this.backups = backups;
        currentWriter = new BufferedWriter(new java.io.FileWriter(getFileName(),true));
    }

    private BufferedWriter currentWriter() {
        var now = LocalDateTime.now();
        if (this.created.plus(this.duration).isBefore(now)) {
            try {
                this.currentWriter.close();
                this.cleanup();
                this.currentWriter = new BufferedWriter(new java.io.FileWriter(getFileName(), true));
                this.created = LocalDateTime.now();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return currentWriter;
    }

    private void cleanup() {
        try {
            var pathMatcher = FileSystems.getDefault().getPathMatcher("glob:"+this.pattern);
            var files = Files.walk(directory).filter(pathMatcher::matches).toList();
            if (files.size() > backups) {
                files = new ArrayList<>(files);
                files.sort((f1, f2) -> {
                    try {
                        var a1 = Files.readAttributes(f1, BasicFileAttributes.class);
                        var a2 = Files.readAttributes(f2, BasicFileAttributes.class);
                        return a1.creationTime().compareTo(a2.creationTime());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return 0;
                });
                for (int i=0; i < files.size() - backups; i++) {
                    System.out.println("Deleting file: " + files.get(i));
                    Files.deleteIfExists(files.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFileName() {
        var name = pattern.replaceAll("\\*", this.filename.get());
        return name;
    }

    @Override
    public void write(L.Level l, CharSequence s) throws IOException {
        currentWriter().append(s).append('\n');
    }

    @Override
    public void close() throws IOException {
        this.currentWriter.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        AtomicInteger cnt= new AtomicInteger();
        var writer = new RollingFileWriter(Duration.ofSeconds(3), Paths.get(""), "application_*.log", () -> String.valueOf(cnt.getAndIncrement()), 3);

        for (int i=0; i < 10; i++) {
            writer.write(L.Level.DEBUG, "Foooo");
            Thread.sleep(1000);
            System.out.println("Written log");
        }

        writer.close();
    }
}
