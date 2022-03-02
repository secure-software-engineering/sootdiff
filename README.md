![Maven Build](https://github.com/secure-software-engineering/sootdiff/actions/workflows/maven.yml/badge.svg)


# SootDiff - Bytecode Comparison Across Different Java Compilers

This repository hosts the SootDiff analysis tool. SootDiff allows the comparison of the Java ByteCode create by different Java compilers.
To do so it uses static analysis and optimizations to unify the generated ByteCode, e.g. Constant Propagation, Dead Code Elimination, String Handling. Its goal is to provide researchers and practitioners with a tool and library on which they can base their own research projects and product implementations. 

## Obtaining SootDiff
You can either build SootDiff on your own using Maven, or you can download a release from here on Github.

### Downloading the Release
The Release Page contains all pre-built JAR files for each release that we officially publish. We recommend using the latest and greatest version unless you have a specific issue that prevents you from doing so. In that case, please let us know.


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
SootDiff is licensed under the MIT license, see LICENSE file. This basically means that you are free to use the tool (even in commercial, closed-source projects). 


# Use SootDiff to compare Jars

The class `src/test/java/MainCompareArtifacts.java` can be used to compare multiple jars on the basis of class sha,tlshs and timestamps and produces a markdown report of that.

Invoke the main method with parameters `-inJars jar1,jar2,jar3 -fileFilter pathToFilterFile.txt`

The filter file can be used to specify classes of interest (e.g. classes fixed in a fixing commit). Make sure to specify the files as they would be located in the analyzed jar, e.g., in a commit the change might be related to `src/main/java/pack/a/b.java`, in the jar it would be referenced from the package onwards and in class format: `pack/a/b.class`. 
