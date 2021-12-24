package template.cons;

public class Type extends Symbol {
    public static Type Void = Type.of("void");
    public static Type Size = Type.of("int");
    public static Type I32 = Type.of("int");
    public static Type F32 = Type.of("floats");

    Type(String name) {
        super(name);
    }

    public static Type of(String name) {
        return new Type(name);
    }

    public Var var(String name) {
        return Var.of(this, name);
    }

    public VarList vars(String... names) {
        return VarList.of(this, names);
    }
}