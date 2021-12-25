#!/bin/bash
clear

rm -rf .class
mkdir -p .class


javac template/*.java  -d .class
java -classpath ".class" template.Main
javac -classpath "lib/lwjgl/*" src/*.java src/SGFX/*.java src/SGFX/Vec/*.java -d .class

if [[ $1 == run ]]; then
    java -classpath ".class:lib/lwjgl/*" src.Main
fi
