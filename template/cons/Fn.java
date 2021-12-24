package template.cons;

public class Fn extends Node {
    public final String name;
    public final VarList args;
    public final Type output;
    public final Block body;

    Fn(String name, Type output, VarList args, Node... inner) {
        this.name = name;
        this.output = output;
        this.args = args;

        String header = output.name;
        if (name.length() > 0) {
            header += " " + name;
        }
        header += args.maptuple(x -> x.def());

        this.body = new Block(header);
        this.body.add(inner);
    }

    public static Fn of(String name, Type output, VarList args, Node... inner) {
        return new Fn(name, output, args, inner);
    }

    public String to_str(int indent, String mod) {
        return body.to_str(indent, mod);
    }
}