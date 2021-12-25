package template;

import java.nio.file.*;
import template.cons.*;

class TemplateVec {
    public final String gl_type;
    public final VarList comps;

    public final Type type_vec;
    public final Type type_scalar;
    public final Type type_arr;
    public final Type type_buf;

    public final Struct vec;
    public final Struct arr;
    public final Struct buf;

    public TemplateVec(String name, VarList comps) {
        this.gl_type = name.toUpperCase();
        this.comps = comps;

        this.vec = Struct.of(name + "x" + comps.len);
        this.arr = Struct.of("Arr");
        this.buf = Struct.subclass("Buf", "GL_Buf");

        this.type_scalar = comps.getScalar();
        this.type_vec = vec.getType();
        this.type_arr = arr.getType();
        this.type_buf = buf.getType();

        this.vec.add_class(this.arr);
        this.vec.add_class(this.buf);
    }

    public void compile_arr() {
        Var data = Type.of(type_scalar.name + "[]").var("data");

        String new_arr = "new " + type_scalar.name + "[" + comps.len + " * N]";

        arr.add_var(data);
        arr.add_mk(Type.Size.vars("N"), Ln.of(data.name, " = ", new_arr));
        arr.add(Fn.of("size", Type.Size, VarList.of(), Ln.ret("data.length / " + comps.len)));

        {
            Node[] inner = {
                    Ln.of(data.type.name, " new_data = ", new_arr),
                    Ln.of("System.arraycopy(data, 0, new_data, 0, Math.min(data.length, new_data.length))"),
                    Ln.of("data = new_data")
            };

            arr.add(Fn.of("resize", Type.Void, Type.Size.vars("N"), inner));
        }

        {
            Node body = Ln.of("System.arraycopy(other, " + comps.len, " * index + offset, data, 0, other.length)");
            VarList args = VarList.of(Type.of(type_scalar.name + "[]").var("other"), Type.Size.var("index"),
                    Type.Size.var("offset"));
            arr.add(Fn.of("set", Type.Void, args, body));
        }

        {
            Node body = Ln.of("System.arraycopy(other.data, " + comps.len, " * index + offset, data, 0, other.size())");
            VarList args = VarList.of(type_arr.var("other"), Type.Size.var("index"), Type.Size.var("offset"));
            arr.add(Fn.of("set", Type.Void, args, body));
        }

        {
            VarList args = VarList.of(Type.Size.var("index"), type_vec.var("vec"));
            Node[] body = comps.map((i, x) -> Ln.of("data[" + comps.len, " * index + " + i, "] = vec.", x.name));
            arr.add(Fn.of("set", Type.Void, args, body));
        }

        {
            VarList args = Type.Size.vars("index").concat(comps);
            Node[] body = comps.map((i, x) -> Ln.of("data[" + comps.len, " * index + " + i, "] = ", x.name));
            arr.add(Fn.of("set", Type.Void, args, body));
        }

        {
            String fargs = comps.maptuple((i, x) -> "data[" + comps.len + " * index + " + i + "]");
            Node fbody = Ln.ret("new ", type_vec.name, fargs);
            arr.add(Fn.of("get", type_vec, Type.Size.vars("index"), fbody));
        }
    }

    public void compile_buf(Type BufFmt_Usage) {
        buf.add(new Struct.Member(type_arr.var("local")).set_final());
        {
            VarList args = VarList.of(Type.Size.var("target"), BufFmt_Usage.var("usage"), Type.Size.var("reserve"));

            Ln[] body = {
                Ln.of("super(target, BufFmt.Block(BufFmt.Type." + gl_type + ", " + comps.len + ", usage))"),
                Ln.of("local = new Arr(reserve)")
            };

            buf.add_mk(args, body);
        }

        buf.add(Fn.of("getLen", Type.Size, VarList.of(), Ln.ret("local.size()")));

        {
            Ln[] body = {
                    Ln.of("bind()"),
                    Ln.of("glBufferData(target, local.data, fmt.usage.gl_value)"),
                    Ln.of("unbind()")
            };
            buf.add(Fn.of("update", Type.Void, VarList.of(), body));
        }


        VarList args_unaligned = Type.Size.vars("index", "offset");
        {
            VarList args = Type.of(type_scalar.name + "[]").vars("data").concat(args_unaligned);
            Ln[] body = {
                    Ln.of("bind()"),
                    Ln.of("local.set(data, index, offset)"),
                    Ln.of("glBufferSubData(target, ", comps.len + "*index + offset, data)"),
                    Ln.of("unbind()"),
            };
            buf.add(Fn.of("update_range_unaligned", Type.Void, args, body));
        }

        {
            VarList args = type_arr.vars("data").concat(args_unaligned);
            Ln body = Ln.of("update_range_unaligned(data.data, index, offset)");
            buf.add(Fn.of("update_range_unaligned", Type.Void, args, body));    
        }
        {
            VarList args = type_vec.vars("data").concat(args_unaligned);
            Ln body = Ln.of("update_range_unaligned(data.as_array(), index, offset)");
            buf.add(Fn.of("update_range_unaligned", Type.Void, args, body));    
        }

        {
            VarList args = type_arr.vars("data").concat(Type.Size.vars("index"));
            Ln body = Ln.of("update_range_unaligned(data.data, index, 0)");
            buf.add(Fn.of("update_range", Type.Void, args, body));
        }
        {
            VarList args = type_vec.vars("data").concat(Type.Size.vars("index"));
            Ln body = Ln.of("update_range_unaligned(data.as_array(), index, 0)");
            buf.add(Fn.of("update_range", Type.Void, args, body));
        }
    }

    private void fnPair(Fn fn) {
        Ln body = Ln.ret("this.copy().", fn.name, fn.args.maptuple(x -> x.name));
        vec.add(fn);
        vec.add(Fn.of("c" + fn.name, fn.output, fn.args, body));
    }

    private void fn_comp(String name, VarList args, VarList.MapNode fn) {
        Node[] end = { Ln.ret("this") };
        fnPair(Fn.of(name, type_vec, args, Node.concat(comps.map(fn), end)));
    }

    private void fn_op(String name, String op) {
        fn_comp(name, type_vec.vars("other"), x -> Ln.of("this.", x.name, " ", op, "= other.", x.name));
        fn_comp(name, comps, x -> Ln.of("this.", x.name, " ", op, "= ", x.name));
    }

    public void fn_vec(String name, VarList args, Node... body) {
        this.vec.add(Fn.of(name, type_vec, args, body));
    }

    public void fn_scalar(String name, VarList args, Node... body) {
        this.vec.add(Fn.of(name, type_scalar, args, body));
    }

    public void compile_all() {
        comps.foreach(x -> vec.add_var(x));
        vec.add_mk(comps, comps.map(x -> Ln.of("this.", x.name, " = ", x.name)));

        {
            Node fbody = Ln.ret("new ", type_vec.name, comps.maptuple(x -> x.name));
            vec.add(new Struct.Member(Fn.of("of", type_vec, comps, fbody)).set_static());
            vec.add(Fn.of("copy", type_vec, VarList.of(), fbody));
        }

        {
            Type type_array = Type.of(type_scalar.name + "[]");
            Ln[] body = {
                Ln.of(type_array.name, " data = { ", comps.mapjoin(", ", x -> x.name), " }"),
                Ln.ret("data")
            };
            vec.add(Fn.of("as_array", type_array, VarList.of(), body));
        }

        fn_op("add", "+");
        fn_op("sub", "-");
        fn_op("mul", "*");
        fn_op("div", "/");

        fn_comp("reset", Type.Void.vars(), x -> Ln.of(x.name + " = 0"));
        fn_comp("reset", type_scalar.vars("value"), x -> Ln.of(x.name + " = value"));

        {
            VarList arg = type_vec.vars("other");

            fn_scalar("dot", arg, Ln.ret(comps.mapjoin(" + ", x -> x.name + "*other." + x.name)));
            fn_scalar("dist", arg, Ln.ret("this.csub(other).mag()"));
            fn_scalar("distSq", arg, Ln.ret("this.csub(other).magSq()"));

            String comb_arg = comps.maptuple(x -> x.name);

            fn_scalar("dot", comps, Ln.ret(comps.mapjoin(" + ", x -> x.name + "*" + x.name)));
            fn_scalar("dist", comps, Ln.ret("this.csub", comb_arg, ".mag()"));
            fn_scalar("distSq", comps, Ln.ret("this.csub", comb_arg, ".magSq()"));
        }

        {
            VarList arg = VarList.of();
            fn_comp("abs", arg, x -> Ln.of(x.name, " = Math.abs(", x.name, ")"));
            fn_comp("inv", arg, x -> Ln.of(x.name, " = 1/", x.name));

            fn_scalar("sum", arg, Ln.ret(comps.mapjoin(" + ", x -> x.name)));
            fn_scalar("prod", arg, Ln.ret(comps.mapjoin(" * ", x -> x.name)));

            fn_scalar("magSq", arg, Ln.ret("this.dot(this)"));
            fn_scalar("mag", arg, Ln.ret("(" + type_scalar.name + ")Math.sqrt(this.dot(this))"));

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

            fnPair(Fn.of("Jconj", type_vec, VarList.of(), Ln.of("y = -y"), Ln.ret("this")));
            fnPair(Fn.of("Jinv", type_vec, VarList.of(),
                    Ln.of(type_scalar.name, " mag = x * x + y * y"),
                    Ln.of("x /= mag"),
                    Ln.of("y /= mag"),
                    Ln.ret("this")));
            fnPair(Fn.of("Jmul", type_vec, type_vec.vars("other"),
                    Ln.of(type_scalar.name, " a = x * other.x + y * other.y"),
                    Ln.of(type_scalar.name, " b = x * other.y - y * other.x"),
                    Ln.of("x = a"),
                    Ln.of("y = b"),
                    Ln.ret("this")));
        }

        compile_arr();
        compile_buf(Type.of("BufFmt.Usage"));
    }

    public static Struct compile(boolean can_index, String prefix, VarList comps) {
        TemplateVec template = new TemplateVec(prefix, comps);
        template.compile_all();

        Type fmt_usage = Type.of("BufFmt.Usage");

        VarList args = VarList.of(fmt_usage.var("usage"), Type.Size.var("reserve"));
        VarList binding = Type.Size.vars("binding");

        {
            Struct sub = Struct.subclass("Storage", "Buf", "Buf.Storage");
            sub.add_mk(args, Ln.of("super(GL_SHADER_STORAGE_BUFFER, usage, reserve)"));
            sub.add(Fn.of("set_binding", Type.Void, binding,
                    Ln.of("glBindBufferBase(GL_SHADER_STORAGE_BUFFER, binding, gl_handle)")));
            template.vec.add_class(sub);
        }

        {
            Struct sub = Struct.subclass("Uniform", "Buf", "Buf.Uniform");
            sub.add_mk(args, Ln.of("super(GL_UNIFORM_BUFFER, usage, reserve)"));
            sub.add(Fn.of("set_binding", Type.Void, binding,
                    Ln.of("glBindBufferBase(GL_UNIFORM_BUFFER, binding, gl_handle)")));
            template.vec.add_class(sub);
        }

        {
            Struct sub = Struct.subclass("Attrib", "Buf", "Buf.Attrib");
            sub.add_mk(args, Ln.of("super(GL_ARRAY_BUFFER, usage, reserve)"));
            template.vec.add_class(sub);
        }

        if (can_index) {
            Struct sub = Struct.subclass("Index", "Buf", "Buf.Index");
            sub.add_mk(args, Ln.of("super(GL_ELEMENT_ARRAY_BUFFER, usage, reserve)"));
            template.vec.add_class(sub);
        }

        return template.vec;
    }
}

public class Main {
    public static void compile_vec_with(String... comps) {
        Type[] type_list = {
                Type.of("int"),
                Type.of("int"),
                Type.of("float")
        };

        Struct[] vec_list = {
                TemplateVec.compile(true, "u32", type_list[0].vars(comps)),
                TemplateVec.compile(false, "i32", type_list[1].vars(comps)),
                TemplateVec.compile(false, "f32", type_list[2].vars(comps)),
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
            String src = "package src.SGFX.Vec;" + Node.newline;

            src += "import src.SGFX.*;" + Node.newline;
            src += "import static org.lwjgl.opengl.GL43C.*;" + Node.newline;
            src += "// Generated class: Refer template/Main.java" + Node.newline;
            src += "public ";

            for (Struct vec : vec_list) {
                String filename = vec.name + ".java";

                try {
                    Path path = Paths.get("src", "SGFX", "Vec", filename);
                    Files.write(path, (src + vec.to_str(0)).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                } catch (Exception ex) {
                    System.out.println("Failed: " + filename);
                    System.out.println(ex.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        compile_vec_with("x");
        compile_vec_with("x", "y");
        compile_vec_with("x", "y", "z");
        compile_vec_with("x", "y", "z", "w");
    }
}