#!/usr/bash

rm -rf .class
mkdir -p .class

javac template/*.java  -d .class
java -classpath ".class" template.Main

javac -classpath "lib/lwjgl/*" src/*.java src/SGFX/*.java src/Vec/*.java -d .class
java -classpath ".class:lib/lwjgl/*" src.Main