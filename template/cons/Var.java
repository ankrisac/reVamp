package template.cons;

public class Var extends Symbol {
    public final Type type;

    Var(Type type, String name) {
        super(name);
        this.type = type;
    }

    public static Var of(Type type, String name) {
        return new Var(type, name);
    }

    public static Var of(String type, String name) {
        return new Var(Type.of(type), name);
    }

    public String def() {
        return this.type.name + " " + this.name;
    }

    public String let(String value) {
        return def() + " = " + value;
    }

    public VarList lst() {
        return VarList.of(this);
    }
}