SRC := $(wildcard src/*.java)
TEMPLATE := $(wildcard template/*.java)


.class/template/Main.class: $(TEMPLATE)
	javac template/*.java  -d .class
	java -classpath ".class" template.Main

.class/src/Main.class: $(SRC) .class/template/Main.class
	javac -classpath "lib/lwjgl/*" src/*.java src/SGFX/*.java src/SGFX/Vec/*.java -d .class

.PHONY: run
run: .class/src/Main.class
	java -classpath ".class:lib/lwjgl/*" src.Main
