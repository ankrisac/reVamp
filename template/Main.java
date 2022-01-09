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
        Type prim_arr = Type.of(type_scalar.name + "[]");
        Var data = prim_arr.var("data");

        String new_arr = "new " + type_scalar.name + "[" + comps.len + " * N]";

        arr.add_var(data);
        arr.add_mk(Type.Size.vars("N"), Ln.of(data.name, " = ", new_arr));
        arr.add(Fn.of("size", Type.Size, VarList.of(), Ln.ret("data.length / " + comps.len)));

        {

            Node[] body = {
                    Block.If("data.length % " + comps.len + " != 0",
                            Ln.of("throw new RuntimeException(\"Data length must be multiple of vector dimension\")")),
                    Ln.of("Arr out = new Arr(data.length)"),
                    Ln.of("out.set_unaligned(data, 0, 0)"),
                    Ln.ret("out")
            };

            Fn of = Fn.of("of", type_arr, Type.of(type_scalar.name + "...").vars("data"), body);
            arr.add(new Struct.Member(of).set_static());
        }

        {
            Node[] inner = {
                    Ln.of(data.type.name, " new_data = ", new_arr),
                    Ln.of("System.arraycopy(data, 0, new_data, 0, Math.min(data.length, new_data.length))"),
                    Ln.of("data = new_data")
            };

            arr.add(Fn.of("resize", Type.Void, Type.Size.vars("N"), inner));
        }

        arr.add(Fn.of("set_unaligned", Type.Void,
                VarList.of(prim_arr.var("other"), Type.Size.var("index"), Type.Size.var("offset")),
                Ln.of("System.arraycopy(other, " + comps.len, " * index + offset, data, 0, other.length)")));

        arr.add(Fn.of("set_unaligned", Type.Void,
                VarList.of(type_arr.var("other"), Type.Size.var("index"), Type.Size.var("offset")),
                Ln.of("System.arraycopy(other.data, " + comps.len, " * index + offset, data, 0, other.size())")));

        arr.add(Fn.of("set", Type.Void,
                VarList.of(Type.Size.var("index"), type_vec.var("vec")),
                comps.map((i, x) -> Ln.of("data[" + comps.len, " * index + " + i, "] = vec.", x.name))));

        arr.add(Fn.of("set", Type.Void,
                Type.Size.vars("index").concat(comps),
                comps.map((i, x) -> Ln.of("data[" + comps.len, " * index + " + i, "] = ", x.name))));

        {
            String fargs = comps.maptuple((i, x) -> "data[" + comps.len + " * index + " + i + "]");
            arr.add(Fn.of("get", type_vec, Type.Size.vars("index"), Ln.ret("new ", type_vec.name, fargs)));
        }
    }

    public void compile_buf(Type BufFmt_Usage) {
        buf.add_var(type_arr.var("local"));

        {
            VarList args = VarList.of(Type.Size.var("target"), BufFmt_Usage.var("usage"), Type.Size.var("reserve"));
            Ln[] body = {
                Ln.of("super(target, BufFmt.Block(BufFmt.Type." + gl_type + ", " + comps.len + ", usage))"),
                Ln.of("local = new Arr(reserve)")
            };
            buf.add_mk(args, body);
        }

        {
            Node[] body = {
                Block.If("realloc", 
                    Ln.of("bind()"),
                    Ln.of("glBufferData(target, local.data, fmt.usage.gl_value)"),
                    Ln.of("unbind()")            
                ),
                Block.Else(
                    Ln.of("write(local, 0)")
                )
            };

            buf.add(Fn.of("update", Type.Void, Type.of("boolean").vars("realloc"), body));
        }

        VarList args_unaligned = Type.Size.vars("index", "offset");
        VarList args_aligned = Type.Size.vars("index");

        {
            VarList args = Type.of(type_scalar.name + "[]").vars("data").concat(args_unaligned);
            Ln[] body = {
                    Ln.of("bind()"),
                    Ln.of("glBufferSubData(target, ", comps.len + "*index + offset, data)"),
                    Ln.of("unbind()"),
            };
            buf.add(Fn.of("write_unaligned", Type.Void, args, body));
        }

        buf.add(Fn.of("write_unaligned", Type.Void,
                type_arr.vars("data").concat(args_unaligned),
                Ln.of("write_unaligned(data.data, index, offset)")));

        buf.add(Fn.of("write_unaligned", Type.Void,
                type_vec.vars("data").concat(args_unaligned),
                Ln.of("write_unaligned(data.as_array(), index, offset)")));

        buf.add(Fn.of("write", Type.Void,
                type_arr.vars("data").concat(args_aligned),
                Ln.of("write_unaligned(data.data, index, 0)")));

        buf.add(Fn.of("write", Type.Void,
                type_vec.vars("data").concat(args_aligned),
                Ln.of("write_unaligned(data.as_array(), index, 0)")));
    }

    private void addFnPair(Fn fn) {
        Ln body = Ln.ret("this.copy().", fn.name, fn.args.maptuple(x -> x.name));
        vec.add(fn);
        vec.add(Fn.of("c" + fn.name, fn.output, fn.args, body));
    }

    public void addFnVec(String name, VarList args, Node... body) {
        vec.add(Fn.of(name, type_vec, args, body));
    }

    public void addFnScalar(String name, VarList args, Node... body) {
        vec.add(Fn.of(name, type_scalar, args, body));
    }

    private Fn fn_comp(String name, VarList args, VarList.MapNode fn) {
        Node[] end = { Ln.ret("this") };
        return Fn.of(name, type_vec, args, Node.concat(comps.map(fn), end));
    }

    interface StrBinFn {
        public String apply(String x, String y);
    }
    private void addFnPiece(String name, StrBinFn fn) {
        addFnPair(fn_comp(name, type_vec.vars("other"), x -> Ln.of(fn.apply("this." + x.name, "other." + x.name))));
        addFnPair(fn_comp(name, comps, x -> Ln.of(fn.apply("this." + x.name, x.name))));
    }
    private void addFnOp(String name, String op) {
        addFnPiece(name, (x, y) -> x + " " + op + "= " + y);
    }
    private void addFnScalarOp(String name, String op) {
        addFnPair(fn_comp(name, type_scalar.vars("other"), x -> Ln.of("this.", x.name, " ", op, "= other")));
    }

    public void compile_all() {
        comps.foreach(x -> vec.add_var(x));
        vec.add_mk(comps, comps.map(x -> Ln.of("this.", x.name, " = ", x.name)));

        {
            Node fbody = Ln.ret("new ", type_vec.name, comps.maptuple(x -> x.name));
            vec.add(Fn.of("copy", type_vec, VarList.of(), fbody));
            vec.add(new Struct.Member(Fn.of("of", type_vec, comps, fbody)).set_static());
        }

        vec.add(Fn.of("toString", Type.of("String"), Type.Void.vars(), Ln.ret("\"[\" + ", comps.mapjoin(" + \",\" + ", x -> x.name), " + \"]\"")));

        {
            Fn zero = Fn.of("zero", type_vec, Type.Void.vars(), Ln.ret("of", comps.maptuple(x -> "0")));
            vec.add(new Struct.Member(zero).set_static());

            Fn val = Fn.of("val", type_vec, type_scalar.vars("val"), Ln.ret("of", comps.maptuple(x -> "val")));
            vec.add(new Struct.Member(val).set_static());
        }
        vec.add(fn_comp("set", comps, x -> Ln.of("this.", x.name, " = ", x.name)));
        vec.add(fn_comp("set", type_vec.vars("other"), x -> Ln.of("this.", x.name, " = other.", x.name)));

        {
            Type type_array = Type.of(type_scalar.name + "[]");
            Ln[] body = {
                    Ln.of(type_array.name, " data = { ", comps.mapjoin(", ", x -> x.name), " }"),
                    Ln.ret("data")
            };
            vec.add(Fn.of("as_array", type_array, VarList.of(), body));
        }

        addFnOp("add", "+");
        addFnOp("sub", "-");
        addFnOp("mul", "*");
        addFnOp("div", "/");

        addFnPiece("max", (x, y) -> x + " = Math.max(" + x + "," + y + ")");
        addFnPiece("min", (x, y) -> x + " = Math.min(" + x + "," + y + ")");
        
        addFnScalarOp("sadd", "+");
        addFnScalarOp("ssub", "-");
        addFnScalarOp("smul", "*");
        addFnScalarOp("sdiv", "/");

        vec.add(fn_comp("reset", Type.Void.vars(), x -> Ln.of(x.name + " = 0")));
        vec.add(fn_comp("reset", type_scalar.vars("value"), x -> Ln.of(x.name + " = value")));


        {
            VarList arg = type_vec.vars("other");

            addFnScalar("dot", arg, Ln.ret(comps.mapjoin(" + ", x -> x.name + "*other." + x.name)));
            addFnScalar("dist", arg, Ln.ret("this.csub(other).mag()"));
            addFnScalar("distSq", arg, Ln.ret("this.csub(other).magSq()"));

            String comb_arg = comps.maptuple(x -> x.name);

            addFnScalar("dot", comps, Ln.ret(comps.mapjoin(" + ", x -> x.name + "*" + x.name)));
            addFnScalar("dist", comps, Ln.ret("this.csub", comb_arg, ".mag()"));
            addFnScalar("distSq", comps, Ln.ret("this.csub", comb_arg, ".magSq()"));
        }

        {
            VarList arg = VarList.of();
            addFnPair(fn_comp("abs", arg, x -> Ln.of(x.name, " = Math.abs(", x.name, ")")));
            addFnPair(fn_comp("inv", arg, x -> Ln.of(x.name, " = 1/", x.name)));

            addFnScalar("sum", arg, Ln.ret(comps.mapjoin(" + ", x -> x.name)));
            addFnScalar("prod", arg, Ln.ret(comps.mapjoin(" * ", x -> x.name)));

            addFnScalar("magSq", arg, Ln.ret("this.dot(this)"));
            addFnScalar("mag", arg, Ln.ret("(" + type_scalar.name + ")Math.sqrt(this.dot(this))"));

            {
                String max = comps.vars[0].name;
                String min = max;

                for (int i = 1; i < comps.len; i++) {
                    String x = comps.vars[i].name;
                    max = "Math.max(" + max + "," + x + ")";
                    min = "Math.min(" + min + "," + x + ")";
                }

                addFnScalar("max", arg, Ln.ret(max));
                addFnScalar("min", arg, Ln.ret(min));
            }
        }

        if (comps.len == 2) {
            addFnScalar("grad", VarList.of(), Ln.ret("y / x"));
            addFnScalar("invgrad", VarList.of(), Ln.ret("x / y"));

            addFnPair(Fn.of("Jconj", type_vec, VarList.of(), Ln.of("y = -y"), Ln.ret("this")));
            addFnPair(Fn.of("Jinv", type_vec, VarList.of(),
                    Ln.of(type_scalar.name, " mag = x * x + y * y"),
                    Ln.of("x /= mag"),
                    Ln.of("y /= mag"),
                    Ln.ret("this")));
            addFnPair(Fn.of("Jmul", type_vec, type_vec.vars("other"),
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
                TemplateVec.compile(comps.length == 1, "u32", type_list[0].vars(comps)),
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
                    Files.write(path, (src + vec.to_str(0)).getBytes(), StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING);
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