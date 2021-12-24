package template;

import java.nio.file.*;

import template.cons.*;

class TemplateVec {
    public final VarList comps;
    public final Type type;
    public final Type scalar;
    public final Struct struct;

    public TemplateVec(String name, VarList comps) {
        this.comps = comps;
        this.struct = new Struct(name);
        this.type = struct.getType();
        this.scalar = comps.getScalar();
    }

    public Struct compile_arr() {
        Struct out = new Struct("Arr");
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
            Node body = Ln.of("System.arraycopy(other.data, " + comps.len, " * index + offset, data, 0, other.size())");
            VarList args = VarList.of(out.getType().var("other"), Type.Size.var("index"), Type.Size.var("offset"));
            out.add(Fn.of("set", Type.Void, args, body));
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

    public Struct compile_buf() {
        Struct out = new Struct("Buf");
        return out;
    }

    private void fnPair(Fn fn) {
        Ln body = Ln.ret("this.copy().", fn.name, fn.args.maptuple(x -> x.name));
        struct.add(fn);
        struct.add(Fn.of("c" + fn.name, fn.output, fn.args, body));
    }

    private void fn_comp(String name, VarList args, VarList.MapNode fn) {
        Node[] end = { Ln.ret("this") };
        fnPair(Fn.of(name, type, args, Node.concat(comps.map(fn), end)));
    }

    private void fn_op(String name, String op) {
        {
            VarList.MapNode fn = x -> Ln.of("this.", x.name, " ", op, "= other.", x.name);
            fn_comp(name, type.vars("other"), fn);    
        }

        {
            VarList.MapNode fn = x -> Ln.of("this.", x.name, " ", op, "= ", x.name);
            fn_comp(name, comps, fn);    
        }
    }

    public void fn_vec(String name, VarList args, Node... body) {
        this.struct.add(Fn.of(name, type, args, body));
    }

    public void fn_scalar(String name, VarList args, Node... body) {
        this.struct.add(Fn.of(name, scalar, args, body));
    }

    public Struct compile() {
        comps.foreach(x -> struct.add_var(x));
        struct.add_mk(comps, comps.map(x -> Ln.of("this.", x.name, " = ", x.name)));

        {
            Node fbody = Ln.ret("new ", type.name, comps.maptuple(x -> x.name));
            struct.add(new Struct.Member(Fn.of("of", type, comps, fbody)).set_static());
            struct.add(Fn.of("copy", type, VarList.of(), fbody));
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

        struct.add_class(compile_arr());
        return struct;
    }

    public static void compile(String... comps) {
        int N = comps.length;

        Type i32 = Type.of("int");
        Type f32 = Type.of("float");

        Struct ivec = new TemplateVec("ivec" + N, i32.vars(comps)).compile();
        Struct fvec = new TemplateVec("fvec" + N, f32.vars(comps)).compile();

        VarList.MapStr fn = x -> "(" + x.type.name + ")" + x.name;
        ivec.add(Fn.of("f", fvec.getType(), VarList.of(), Ln.ret("new ", fvec.name, f32.vars(comps).maptuple(fn))));
        fvec.add(Fn.of("i", ivec.getType(), VarList.of(), Ln.ret("new ", ivec.name, i32.vars(comps).maptuple(fn))));

        {
            VarList arg = Type.Size.vars("index");
            String inputs = "(index, " + i32.vars(comps).mapjoin(", ", x -> x.name) + ")";

            ivec.add(Fn.of("gl_send", Type.Void, arg, Ln.of("glUniform" + N, "i", inputs)));    
            fvec.add(Fn.of("gl_send", Type.Void, arg, Ln.of("glUniform" + N, "f", inputs)));    
        }

        {
            String src = "package src.Vec;" + Node.newline;
            src += "import static org.lwjgl.opengl.GL43C.*;" + Node.newline;
            src += "// Generated class: Refer template/Main.java" + Node.newline;
            src += "public ";

            write(src + ivec.to_str(0), ivec.name);
            write(src + fvec.to_str(0), fvec.name);
        }
    }

    public static void write(String src, String filename) {
        try {
            Path path = Paths.get("src", "Vec", filename + ".java");
            Files.write(path, src.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
        catch (Exception ex) {
            System.out.println("Failed: " + filename);
            System.out.println(ex.getMessage());
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