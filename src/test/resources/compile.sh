#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

cd $DIR


rm -rf reference

rm -rf 1.5
rm -rf 1.6
rm -rf 1.7
rm -rf 1.8

rm -rf ecj1.5
rm -rf ecj1.6
rm -rf ecj1.7
rm -rf ecj1.8


rm -rf gcj1.5
rm -rf gcj1.6
rm -rf gcj1.7
rm -rf gcj1.8




mkdir reference

mkdir 1.5
mkdir 1.6
mkdir 1.7
mkdir 1.8
mkdir 1.9

mkdir ecj1.5
mkdir ecj1.6
mkdir ecj1.7
mkdir ecj1.8


mkdir gcj1.5
mkdir gcj1.6
mkdir gcj1.7
mkdir gcj1.8


find . -iname "*.class" -exec rm "{}" \;


javac -d reference java/*.java
javac -d 1.5 -source 1.5 -target 1.5 java/*.java
javac -d 1.6 -source 1.6 -target 1.6 java/*.java
javac -d 1.7 -source 1.7 -target 1.7 java/*.java
javac -d 1.8 -source 1.8 -target 1.8 java/*.java
javac -d 1.9 -source 1.9 -target 1.9 java/*.java
#/usr/libexec/java_home -v 14 --exec javac -d 1.9 -source 1.9 -target 1.9 java/*.java  <-- Use on Mac, if JDK1.8 is your default! The line above crashes

CLASSPATH_RT="$JAVA_HOME/jre/lib/rt.jar"

echo $CLASSPATH_RT

echo "Compiling with ECJ"

java -jar ecj-3.15.1.jar -1.5 -classpath "$CLASSPATH_RT" -d ecj1.5 java/*.java
java -jar ecj-3.15.1.jar -1.6 -classpath "$CLASSPATH_RT" -d ecj1.6 java/*.java
java -jar ecj-3.15.1.jar -1.7 -classpath "$CLASSPATH_RT" -d ecj1.7 java/*.java
java -jar ecj-3.15.1.jar -1.8 -classpath "$CLASSPATH_RT" -d ecj1.8 java/*.java

#gcj -d gcj1.5 -C -ftarget=1.5 java/*.java
#gcj -d gcj1.6 -C -ftarget=1.6 java/*.java
#gcj -d gcj1.7 -C -fsource=1.7 -ftarget=1.7 java/*.java
#gcj -d gcj1.8 -C -ftarget=1.8 java/*.java
