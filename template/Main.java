package template;

import java.util.ArrayList;

import java.nio.file.*;
import java.io.IOException;

class Symbol {
    public final String name;

    Symbol(String name) {
        this.name = name;
    }
}

class Type extends Symbol {
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

class Var extends Symbol {
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

class VarList {
    public final Var[] vars;
    public final int len;

    public VarList(Var... vars) {
        this.vars = vars;
        this.len = vars.length;
    }

    public static VarList of(Var... args) {
        return new VarList(args);
    }

    public static VarList of(Type type, String... names) {
        Var[] args = new Var[names.length];
        for (int i = 0; i < names.length; i++) {
            args[i] = Var.of(type, names[i]);
        }
        return new VarList(args);
    }

    interface MapStr {
        public String apply(Var var);
    }

    interface IMapStr {
        public String apply(int index, Var var);
    }

    public String mapjoin(String sep, IMapStr fn) {
        String out = "";
        int len = this.len - 1;
        if (len < 0) {
            return out;
        }
        for (int i = 0; i < len; i++) {
            out += fn.apply(i, vars[i]) + sep;
        }
        return out + fn.apply(len, vars[len]);
    }

    public String mapjoin(String sep, MapStr fn) {
        return mapjoin(sep, (i, x) -> fn.apply(x));
    }

    public String mapjoin(MapStr fn) {
        return mapjoin("", fn);
    }

    public String mapjoin(IMapStr fn) {
        return mapjoin("", fn);
    }

    public String maptuple(MapStr map) {
        return "(" + mapjoin(", ", map) + ")";
    }

    public String maptuple(IMapStr map) {
        return "(" + mapjoin(", ", map) + ")";
    }

    interface MapNode {
        public Node apply(Var in);
    }

    interface IMapNode {
        public Node apply(int index, Var in);
    }

    public Node[] map(IMapNode map) {
        Node[] out = new Node[len];
        for (int i = 0; i < len; i++) {
            out[i] = map.apply(i, vars[i]);
        }
        return out;
    }

    public Node[] map(MapNode map) {
        return map((i, x) -> map.apply(x));
    }

    interface MapEffect {
        public void apply(Var in);
    }

    interface IMapEffect {
        public void apply(int index, Var in);
    }

    public void foreach(IMapEffect map) {
        for (int i = 0; i < len; i++) {
            map.apply(i, vars[i]);
        }
    }

    public void foreach(MapEffect map) {
        foreach((i, x) -> map.apply(x));
    }

    public Type getScalar() {
        if (len == 0) {
            throw new RuntimeException("Empty VarList has no Scalar");
        }
        Type scalar = vars[0].type;
        for (Var var : vars) {
            if (scalar.name != var.type.name) {
                throw new RuntimeException("VarList has multiple types, so Scalar does not exist");
            }
        }
        return scalar;
    }
}

abstract class Node {
    static String tab = "    ";
    static String newline = "\n";

    final public String getIndent(int indent) {
        String out = "";
        for (int i = 0; i < indent; i++) {
            out += tab;
        }
        return out;
    }

    abstract public String to_str(int indent, String mod);

    final public String to_str(int indent) {
        return to_str(indent, "");
    }

    public static Node[] concat(Node[] a, Node[] b) {
        Node[] total = new Node[a.length + b.length];
        int i = 0;
        for (Node elem : a) {
            total[i++] = elem;
        }
        for (Node elem : b) {
            total[i++] = elem;
        }
        return total;
    }
}

class Ln extends Node {
    public final String line;

    public Ln(String line) {
        this.line = line;
    }

    public static Ln of(String begin, String... lines) {
        String out = begin;
        for (String ln : lines) {
            out += ln;
        }
        out += ";";
        return new Ln(out);
    }

    public static Ln ret(String... lines) {
        return Ln.of("return ", lines);
    }

    public static Ln text(String... lines) {
        String out = "";
        for (String ln : lines) {
            out += ln;
        }
        return new Ln(out);
    }

    public String to_str(int indent, String mod) {
        return getIndent(indent) + mod + this.line;
    }
}

class Block extends Node {
    public ArrayList<Node> members;
    public final String name;

    public Block(String name) {
        members = new ArrayList<Node>();
        this.name = name;
    }

    public void add(Node node) {
        members.add(node);
    }

    public void add(Node... nodes) {
        for (Node node : nodes) {
            members.add(node);
        }
    }

    public String to_str(int indent, String mod) {
        String out = getIndent(indent) + mod + name + " {" + newline;
        int len = members.size() - 1;
        if (len >= 0) {
            for (int i = 0; i < len; i++) {
                out += members.get(i).to_str(indent + 1, "") + newline;
            }
            out += members.get(len).to_str(indent + 1, "") + newline;
        }
        return out + getIndent(indent) + "}";
    }

    public static Block of(String name, Node... inner) {
        Block out = new Block(name);
        for (Node node : inner) {
            out.add(node);
        }
        return out;
    }

    public static Block Cond(String name, String cond, Node... inner) {
        return Block.of(name + " (" + cond + ")", inner);
    }

    public static Block If(String cond, Node... inner) {
        return Block.Cond("if", cond, inner);
    }

    public static Block ElseIf(String cond, Node... inner) {
        return Block.Cond("else if", cond, inner);
    }

    public static Block Else(String cond, Node... inner) {
        return Block.of("else", inner);
    }

    public static Block While(String cond, Node... inner) {
        return Block.Cond("while", cond, inner);
    }
}

class Fn extends Node {
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

class Class extends Node {
    public static class Member extends Node {
        boolean m_public;
        boolean m_static;
        boolean m_final;
        boolean m_abstract;
        Node node;

        Member(Var var) {
            this.node = Ln.of(var.def());
            m_public = true;
            m_static = false;
            m_final = false;
            m_abstract = false;
        }

        Member(Node node) {
            this.node = node;
            m_public = true;
            m_static = false;
            m_final = false;
            m_abstract = false;
        }

        public Member set_private() {
            m_public = false;
            return this;
        }

        public Member set_static() {
            m_static = true;
            return this;
        }

        public Member set_final() {
            m_final = true;
            return this;
        }

        public Member set_abstract() {
            m_abstract = true;
            return this;
        }

        public String to_str(int indent, String mod) {
            if (m_public) {
                mod += "public ";
            }
            if (m_abstract) {
                mod += "abstract ";
            }
            if (m_static) {
                mod += "static ";
            }
            if (m_final) {
                mod += "final ";
            }
            return node.to_str(indent, mod);
        }
    }

    public final String name;
    public final Block body;

    public Class(String name) {
        this.name = name;
        this.body = new Block("class " + name);
    }

    public void add(Member node) {
        body.add(node);
    }

    public void add(Node node) {
        body.add(new Member(node));
    }

    public void add_var(Var var) {
        add(Ln.of(var.def()));
    }

    public void add_class(Class _class) {
        add(new Member(_class).set_static());
    }

    public void add_mk(VarList args, Node... inner) {
        add(Fn.of("", getType(), args, inner));
    }

    public String to_str(int indent, String mod) {
        return body.to_str(indent, mod);
    }

    public Type getType() {
        return new Type(name);
    }
}

class TemplateVec {
    public final VarList comps;
    public final Type type;
    public final Type scalar;
    public final Class body;

    public TemplateVec(String name, VarList comps) {
        this.comps = comps;
        this.body = new Class(name);
        this.type = body.getType();
        this.scalar = comps.getScalar();
    }

    public Class compile_arr() {
        Class out = new Class("Arr");
        Var data = Type.of(scalar.name + "[]").var("data");

        String new_arr = "new " + scalar.name + "[" + comps.len + " * N]";

        out.add_var(data);
        out.add_mk(Type.Size.vars("N"), Ln.of(data.name, " = ", new_arr));
        out.add(Fn.of("size", Type.Size, VarList.of(), Ln.ret("data.length / " + comps.len)));

        {
            Node[] copy = {
                    Ln.of(data.type.name, " new_data = ", new_arr),
                    Ln.of("System.arraycopy(new_data, 0, data, 0, data.length)"),
                    Ln.of("data = new_data")
            };
            Node block = Block.If("N > size()", copy);

            out.add(Fn.of("resize", Type.Void, Type.Size.vars("N"), block));
        }
        {
            Node fbody = Ln.of("System.arraycopy(other.data, " + comps.len, " * index + offset, data, 0, other.size())");
            VarList args = VarList.of(out.getType().var("other"), Type.Size.var("index"), Type.Size.var("offset"));
            out.add(Fn.of("set", Type.Void, args, fbody));
        }

        {
            VarList args = VarList.of(Type.Size.var("index"), type.var("vec"));
            Node[] body = comps.map((i, x) -> Ln.of("data[" + comps.len, " * index + " + i, "] = vec.", x.name));
            out.add(Fn.of("set", Type.Void, args, body));
        }

        {
            String fargs = comps.maptuple((i, x) -> "data[" + comps.len + " * index + " + i + "]");
            Node fbody = Ln.ret("new ", type.name, fargs);
            out.add(Fn.of("get", type, Type.Size.vars("index"), fbody));
        }
        return out;
    }

    public Class compile_buf() {
        Class out = new Class("Buf");
        return out;
    }

    private void fnPair(Fn fn) {
        Ln copy_body = Ln.ret("this.copy().", fn.name, fn.args.maptuple(x -> x.name));
        body.add(fn);
        body.add(Fn.of("c" + fn.name, fn.output, fn.args, copy_body));
    }

    private void fn_comp(String name, VarList args, VarList.MapNode fn) {
        Node[] end = { Ln.ret("this") };
        fnPair(Fn.of(name, type, args, Node.concat(comps.map(fn), end)));
    }

    private void fn_op(String name, String op) {
        VarList.MapNode fn = x -> Ln.of("this.", x.name, " ", op, "= other.", x.name);
        fn_comp(name, type.vars("other"), fn);
    }

    public void fn_vec(String name, VarList args, Node... body) {
        this.body.add(Fn.of(name, type, args, body));
    }

    public void fn_scalar(String name, VarList args, Node... body) {
        this.body.add(Fn.of(name, scalar, args, body));
    }

    public Class compile() {
        comps.foreach(x -> body.add_var(x));
        body.add_mk(comps, comps.map(x -> Ln.of("this.", x.name, " = ", x.name)));

        {
            Node fbody = Ln.ret("new ", type.name, comps.maptuple(x -> x.name));
            body.add(new Class.Member(Fn.of("of", type, comps, fbody)).set_static());
            body.add(Fn.of("copy", type, VarList.of(), fbody));
        }

        fn_op("add", "+");
        fn_op("sub", "-");
        fn_op("mul", "*");
        fn_op("div", "/");

        {
            VarList arg = type.vars("other");

            fn_scalar("dot", arg, Ln.ret(comps.mapjoin(" + ", x -> x.name + "*other." + x.name)));
            fn_scalar("dist", arg, Ln.ret("this.csub(other).mag()"));
            fn_scalar("distSq", arg, Ln.ret("this.csub(other).magSq()"));
        }

        {
            VarList arg = VarList.of();
            fn_comp("abs", arg, x -> Ln.of(x.name, " = Math.abs(" + x.name + ")"));

            fn_scalar("sum", arg, Ln.ret(comps.mapjoin(" + ", x -> x.name)));
            fn_scalar("prod", arg, Ln.ret(comps.mapjoin(" * ", x -> x.name)));

            fn_scalar("magSq", arg, Ln.ret("this.dot(this)"));
            fn_scalar("mag", arg, Ln.ret("(" + scalar.name + ")Math.sqrt(this.dot(this))"));

            {
                String max = comps.vars[0].name;
                String min = max;

                for (int i = 1; i < comps.len; i++) {
                    String x = comps.vars[i].name;
                    max = "Math.max(" + max + "," + x + ")";
                    min = "Math.min(" + min + "," + x + ")";
                }

                fn_scalar("max", arg, Ln.ret(max));
                fn_scalar("min", arg, Ln.ret(min));
            }
        }

        body.add_class(compile_arr());
        return body;
    }

    public static void compile(String... _comps) {
        String[] types = {
                "int",
                "float",
        };
        String[] prefix = { "i", "f" };
        int N = _comps.length;

        for (int i = 0; i < types.length; i++) {

            String classname = prefix[i] + "vec" + N;
            VarList args = Type.of(types[i]).vars(_comps);

            TemplateVec vec = new TemplateVec(classname, args);
            Class out = vec.compile();

            for (int j = 0; j < types.length; j++) {
                if (i != j) {
                    Type output = Type.of(prefix[j] + "vec" + N);
                    String inputs = Type.of(types[j]).vars(_comps).maptuple(x -> "(" + x.type.name + ")" + x.name);

                    Node ret = Ln.ret("new ", output.name, inputs);
                    out.add(Fn.of(prefix[j], output, VarList.of(), ret));
                }
            }

            {
                Node ret = Ln.of("glUniform" + N + prefix[i] + "(index, " + args.mapjoin(", ", x -> x.name) + ")");
                out.add(Fn.of("uniform", Type.Void, Type.Size.vars("index"), ret));
            }


            String src = "package src.Vec;" + Node.newline;
            src += "import static org.lwjgl.opengl.GL43C.*;" + Node.newline;
            src += "// Generated class: Refer template/Main.java" + Node.newline;
            src += "public " + out.to_str(0);

            try {
                Path path = Paths.get("src", "Vec", classname + ".java");
                Files.write(path, src.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
            catch (Exception ex) {
                System.out.println("Failed: " + classname);
                System.out.println(ex.getMessage());
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        TemplateVec.compile("x", "y");
        TemplateVec.compile("x", "y", "z");
        TemplateVec.compile("x", "y", "z", "w");
    }
}