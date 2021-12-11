#nobloat-jlog

## Goals
- Sane defaults
- Provide only minimal required functionality for a useful logging system
  - no config parser or new config language
- Avoid bloat people are unaware of [see CVE-44228](https://www.lunasec.io/docs/blog/log4j-zero-day/)
- Decent performance


## Benchmarks


### Code size
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


### Runtime

```
Benchmark                 (iterations)   Mode  Cnt       Score       Error  Units
B.nobloatlogExceptions             100  thrpt   10   32576,046 ±   852,906  ops/s
B.nobloatlogNoExceptions           100  thrpt   10  291545,319 ± 42703,113  ops/s
B.tinylogExceptions                100  thrpt   10   25017,774 ±  3035,267  ops/s
B.tinylogNoExceptions              100  thrpt   10  126130,440 ±  4414,337  ops/s
```


## Inspiration

### [tinylog](https://tinylog.org/v2/)
- Already complex class structure and configuration parsing

