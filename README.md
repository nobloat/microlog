#nobloat-jlog

## Goals
- Sane defaults
- Provide only minimal required functionality for a useful logging system
- Avoid security risks through complexity [https://www.lunasec.io/docs/blog/log4j-zero-day/](CVE-44228)


## Inspiration

### [tinylog](https://tinylog.org/v2/)
- Already complex class structure and configuration parsing

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