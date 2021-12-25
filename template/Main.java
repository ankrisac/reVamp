package template;

import java.nio.file.*;
import template.cons.*;

class TemplateVec {
    public final String gl_type;
    public final VarList comps;
    public final Type type;
    public final Type scalar;
    public final Struct struct;

    public TemplateVec(String name, VarList comps) {
        this.gl_type = name.toUpperCase();
        this.comps = comps;
        this.struct = Struct.of(name + "x" + comps.len);
        this.type = struct.getType();
        this.scalar = comps.getScalar();
    }

    public Struct compile_arr() {
        Struct out = Struct.of("Arr");
        Var data = Type.of(scalar.name + "[]").var("data");

        String new_arr = "new " + scalar.name + "[" + comps.len + " * N]";

        out.add_var(data);
        out.add_mk(Type.Size.vars("N"), Ln.of(data.name, " = ", new_arr));
        out.add(Fn.of("size", Type.Size, VarList.of(), Ln.ret("data.length / " + comps.len)));

        {
            Node[] inner = {
                    Ln.of(data.type.name, " new_data = ", new_arr),
                    Ln.of("System.arraycopy(data, 0, new_data, 0, Math.min(data.length, new_data.length))"),
                    Ln.of("data = new_data")
            };

            out.add(Fn.of("resize", Type.Void, Type.Size.vars("N"), inner));
        }

        {
            Node body = Ln.of("System.arraycopy(other, " + comps.len, " * index + offset, data, 0, other.length)");
            VarList args = VarList.of(Type.of(scalar.name + "[]").var("other"), Type.Size.var("index"),
                    Type.Size.var("offset"));
            out.add(Fn.of("set", Type.Void, args, body));
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
            Var[] begin = { Type.Size.var("index") };
            VarList args = VarList.of(Var.concat(begin, comps.vars));
            Node[] body = comps.map((i, x) -> Ln.of("data[" + comps.len, " * index + " + i, "] = ", x.name));
            out.add(Fn.of("set", Type.Void, args, body));
        }

        {
            String fargs = comps.maptuple((i, x) -> "data[" + comps.len + " * index + " + i + "]");
            Node fbody = Ln.ret("new ", type.name, fargs);
            out.add(Fn.of("get", type, Type.Size.vars("index"), fbody));
        }
        return out;
    }

    public Struct compile_buf(Type Fmt_usage, Struct arr) {
        Struct out = Struct.subclass("Buf", "GL_Buf");
        out.add_var(arr.getType().var("local"));
        {
            VarList args = VarList.of(Type.Size.var("target"), Fmt_usage.var("usage"));

            String Block = "BufFmt.Type." + gl_type + ", " + comps.len + ", usage";

            out.add_mk(args, Ln.of("super(target, BufFmt.Block(", Block, "))"));
        }

        out.add(Fn.of("getLen", Type.Size, VarList.of(), Ln.ret("local.size()")));

        {
            Ln[] lines = {
                    Ln.of("bind()"),
                    Ln.of("glBufferData(target, local.data, fmt.usage.gl_value)"),
                    Ln.of("unbind()")
            };
            out.add(Fn.of("update", Type.Void, VarList.of(), lines));
        }

        {
            VarList args = VarList.of(arr.getType().var("data"), Type.Size.var("index"), Type.Size.var("offset"));
            Ln[] lines = {
                    Ln.of("bind()"),
                    Ln.of("local.set(data, index, offset)"),
                    Ln.of("glBufferSubData(target, ", comps.len + "*index + offset, data.data)"),
                    Ln.of("unbind()"),
            };
            out.add(Fn.of("update", Type.Void, args, lines));
        }

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

    public Struct compile_vec() {
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
            fn_comp("abs", arg, x -> Ln.of(x.name, " = Math.abs(", x.name, ")"));
            fn_comp("inv", arg, x -> Ln.of(x.name, " = 1/", x.name));

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

        if (comps.len == 2) {
            fn_scalar("grad", VarList.of(), Ln.ret("y / x"));

            fnPair(Fn.of("Jconj", type, VarList.of(), Ln.of("y = -y"), Ln.ret("this")));
            fnPair(Fn.of("Jinv", type, VarList.of(),
                    Ln.of(scalar.name, " mag = x * x + y * y"),
                    Ln.of("x /= mag"),
                    Ln.of("y /= mag"),
                    Ln.ret("this")));
            fnPair(Fn.of("Jmul", type, type.vars("other"),
                    Ln.of(scalar.name, " a = x * other.x + y * other.y"),
                    Ln.of(scalar.name, " b = x * other.y - y * other.x"),
                    Ln.of("x = a"),
                    Ln.of("y = b"),
                    Ln.ret("this")));
        }

        return struct;
    }

    public static Struct compile(boolean can_index, String prefix, VarList comps) {
        Struct out;

        TemplateVec template = new TemplateVec(prefix, comps);
        out = template.compile_vec();
        Struct arr = template.compile_arr();

        Type fmt_usage = Type.of("BufFmt.Usage");
        Struct buf = template.compile_buf(fmt_usage, arr);

        out.add_class(arr);
        out.add_class(buf);

        VarList args = fmt_usage.vars("usage");
        VarList binding = Type.Size.vars("binding");

        {
            Struct sub = Struct.subclass("Storage", "Buf", "Buf.Storage");
            sub.add_mk(args, Ln.of("super(GL_SHADER_STORAGE_BUFFER, usage)"));
            sub.add(Fn.of("set_binding", Type.Void, binding,
                    Ln.of("glBindBufferBase(GL_SHADER_STORAGE_BUFFER, binding, handle)")));
            out.add_class(sub);
        }

        {
            Struct sub = Struct.subclass("Uniform", "Buf", "Buf.Uniform");
            sub.add_mk(args, Ln.of("super(GL_UNIFORM_BUFFER, usage)"));
            sub.add(Fn.of("set_binding", Type.Void, binding,
                    Ln.of("glBindBufferBase(GL_UNIFORM_BUFFER, binding, handle)")));
            out.add_class(sub);
        }

        {
            Struct sub = Struct.subclass("Attrib", "Buf", "Buf.Attrib");
            sub.add_mk(args, Ln.of("super(GL_ARRAY_BUFFER, usage)"));
            out.add_class(sub);
        }

        if (can_index) {
            Struct sub = Struct.subclass("Index", "Buf", "Buf.Index");
            sub.add_mk(Type.of("BufFmt.Usage").vars("usage"), Ln.of("super(GL_ELEMENT_ARRAY_BUFFER, usage)"));
            out.add_class(sub);
        }

        return out;
    }

    public static void compile_all(String... comps) {
        Type[] type_list = {
                Type.of("int"),
                Type.of("int"),
                Type.of("float")
        };

        Struct[] vec_list = {
                compile(true, "u32", type_list[0].vars(comps)),
                compile(false, "i32", type_list[1].vars(comps)),
                compile(false, "f32", type_list[2].vars(comps)),
        };

        String[] uniform_prefix = { "ui", "i", "f" };

        int N = comps.length;
        for (int i = 0; i < vec_list.length; i++) {
            Struct vec = vec_list[i];
            VarList arg = Type.Size.vars("index");
            String inputs = "(index, " + Type.Void.vars(comps).mapjoin(", ", x -> x.name) + ")";

            vec.add(Fn.of("bind", Type.Void, arg, Ln.of("glUniform" + N + uniform_prefix[i] + inputs)));

            for (int j = 0; j < vec_list.length; j++) {
                if (j != i) {
                    Struct other = vec_list[j];
                    String fn_name = other.name.substring(0, 1);

                    VarList.MapStr fn = x -> "(" + x.type.name + ")" + x.name;
                    Ln body = Ln.ret("new ", other.name, type_list[j].vars(comps).maptuple(fn));
                    vec.add(Fn.of(fn_name, other.getType(), VarList.of(), body));
                }
            }
        }

        {
            String src = "package src.Vec;" + Node.newline;

            src += "import src.SGFX.*;" + Node.newline;
            src += "import static org.lwjgl.opengl.GL43C.*;" + Node.newline;
            src += "// Generated class: Refer template/Main.java" + Node.newline;
            src += "public ";

            for (Struct vec : vec_list) {
                write(src + vec.to_str(0), vec.name);
            }
        }
    }

    public static void write(String src, String filename) {
        try {
            Path path = Paths.get("src", "Vec", filename + ".java");
            Files.write(path, src.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception ex) {
            System.out.println("Failed: " + filename);
            System.out.println(ex.getMessage());
        }
    }
}

public class Main {
    public static void main(String[] args) {
        TemplateVec.compile_all("x");
        TemplateVec.compile_all("x", "y");
        TemplateVec.compile_all("x", "y", "z");
        TemplateVec.compile_all("x", "y", "z", "w");
    }
}