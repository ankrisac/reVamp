rm -rf .class
mkdir -p .class

javac -classpath "lib/lwjgl/*" src/*.java src/SGFX/*.java -d .class
java -classpath ".class:lib/lwjgl/*" src.Main