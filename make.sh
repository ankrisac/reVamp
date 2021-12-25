#!/usr/bash

rm -rf .class
mkdir -p .class


if javac template/*.java  -d .class; then 
    if java -classpath ".class" template.Main; then 
        if javac -classpath "lib/lwjgl/*" src/*.java src/SGFX/*.java src/Vec/*.java -d .class; then
            if [[ $1 == run ]]; then        
                java -classpath ".class:lib/lwjgl/*" src.Main
            fi
        fi
    fi
fi