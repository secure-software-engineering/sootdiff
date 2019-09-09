# SootDiff - Bytecode Comparison Across Different Java Compilers

This repository hosts the SootDiff analysis tool. SootDiff statically computes data flows in Android apps and Java programs. Its goal is to provide researchers and practitioners with a tool and library on which they can base their own research projects and product implementations. We are happy to see that FlowDroid is now widely used in academia as well as industry.

## Obtaining SootDiff
You can either build SootDiff on your own using Maven, or you can download a release from here on Github.

### Downloading the Release
The Release Page contains all pre-built JAR files for each release that we officially publish. We recommend using the latest and greatest version unless you have a specific issue that prevents you from doing so. In that case, please let us know (see contact below).


### Building SootDiff with Maven
To build SootDiff with Maven run
```
mvn install
```

or to build a standalone `jar-with-dependencies` run
```
mvn clean compile assembly:single
```


## Publications
If you want to read the details on how SootDiff works, the published paper [SootDiff @SOAP'19,Phoenix, AZ, USA](https://dl.acm.org/citation.cfm?id=3329966) is a good place to start.



## License
SootDiff is licensed under the LGPL license, see LICENSE file. This basically means that you are free to use the tool (even in commercial, closed-source projects). However, if you extend or modify the tool, you must make your changes available under the LGPL as well. This ensures that we can continue to improve the tool as a community effort.

