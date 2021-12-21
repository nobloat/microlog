package org.nobloat.log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.function.Supplier;

public class RollingFileWriter implements L.Writer {

    /** Defines how many historic files should be kept next to the current opened file */
    public int backups;

    private BufferedWriter currentWriter;
    private Path directory;
    private String pattern;
    private String currentFileName;
    private Supplier<String> filenameGenerator;

    public RollingFileWriter(Path directory, String pattern, Supplier<String> filenameGenerator, int backups) throws IOException {
        if (!pattern.contains("*")) {
            throw new IllegalArgumentException("Pattern must contain exactly one asterisk (*)");
        }
        this.filenameGenerator = filenameGenerator;
        this.pattern = pattern;
        this.directory = directory;
        this.backups = backups;
        this.currentFileName = getFileName();
        currentWriter = new BufferedWriter(new java.io.FileWriter(currentFileName,true));
    }

    private BufferedWriter currentWriter() {
        var newFilename = getFileName();
        if (!currentFileName.equals(newFilename)) {
            synchronized (this) {
                if (!currentFileName.equals(newFilename)) {
                    try {
                        this.currentWriter.close();
                        this.cleanup();
                        this.currentFileName = getFileName();
                        this.currentWriter = new BufferedWriter(new java.io.FileWriter(currentFileName, true));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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
                    Files.deleteIfExists(files.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFileName() {
        var name = pattern.replaceAll("\\*", this.filenameGenerator.get());
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

    public static final Supplier<String> WEEKLY = () -> {
        var now = LocalDateTime.now();
        return String.format("%s-W%s",now.getYear(), now.get(ChronoField.ALIGNED_WEEK_OF_YEAR));
    };

    public static final Supplier<String> MONTHLY = () -> {
        var now = LocalDateTime.now();
        return String.format("%s-M%s",now.getYear(),now.getMonthValue());
    };

    public static final Supplier<String> DAILY = () -> DateTimeFormatter.ISO_DATE.format(LocalDateTime.now());
}
