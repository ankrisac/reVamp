clear

javac template/*.java  -d .class
java -classpath ".class" template.Main
javac -classpath "lib/lwjgl/*" src/*.java src/SGFX/*.java src/Vec/*.java -d .class

if [[ $1 == run ]]; then
    java -classpath ".class:lib/lwjgl/*" src.Main
fi
