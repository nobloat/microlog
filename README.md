**This is just a prototype which can be taken for inspiration, it has never been actually used in PROD yet**.

#nobloat-log

A logging system for Java 17+

## Motivation
[see CVE-44228](https://www.lunasec.io/docs/blog/log4j-zero-day/) 

After that I took a deeper look at [tinylog](https://tinylog.org/v2/) which I have been using so far, and to me it appeared not as tiny as it could (should) be.
Therefore I wanted to try how I would implement such a logging system.

## Goals
- Learn how to write a simple but functional logging library for the JVM
- Use sane defaults that provide good logging information out of the box
- Provide only minimal required functionality for a useful logging system
  - no config parser or new config language
  - no Log writer for every possible output scenario, there is only Console and File.
- Avoid bloat people are unaware of [see CVE-44228](https://www.lunasec.io/docs/blog/log4j-zero-day/)
- Decent performance (it should not be a lot worse than tinylog's performance)
- Minimize LoC without sacrificing the other Goals
- Provide a simple to grab single file Logger which can be dropped into any existing project.

## Code size
**~ 140 LoC vs. ~40k LoC**

```
 cloc src/main/java/org/nobloat/log/L.java
       1 text file.
       1 unique file.
       0 files ignored.

github.com/AlDanial/cloc v 1.92  T=0.04 s (24.8 files/s, 4307.5 lines/s)
-------------------------------------------------------------------------------
Language                     files          blank        comment           code
-------------------------------------------------------------------------------
Java                             1             25              1            148
-------------------------------------------------------------------------------
```

```
github.com/AlDanial/cloc v 1.92  T=7.67 s (54.0 files/s, 9697.5 lines/s)
-------------------------------------------------------------------------------
Language                     files          blank        comment           code
-------------------------------------------------------------------------------
Java                           358           8428          20345          31408
Kotlin                           7            521           1543           2708
Maven                           16            128             13           2640
Scala                            8            605           2695           2039
XML                             13             72             41            862
YAML                             4             22              1            144
Markdown                         7             59              0            127
Properties                       1              0              0              1
-------------------------------------------------------------------------------
SUM:                           414           9835          24638          39929
-------------------------------------------------------------------------------
```


### Runtime benchmarks

```
Benchmark                 (iterations)   Mode  Cnt       Score       Error  Units
B.nobloatlogExceptions             100  thrpt   10   32576,046 ±   852,906  ops/s
B.nobloatlogNoExceptions           100  thrpt   10  291545,319 ± 42703,113  ops/s
B.tinylogExceptions                100  thrpt   10   25017,774 ±  3035,267  ops/s
B.tinylogNoExceptions              100  thrpt   10  126130,440 ±  4414,337  ops/s
```


## Inspiration / See Also
- [tinylog](https://tinylog.org/v2/)