package template.cons;

public class VarList {
    public final Var[] vars;
    public final int len;

    public VarList(Var... vars) {
        this.vars = vars;
        this.len = vars.length;
    }

    public VarList concat(VarList other) {
        return VarList.of(Var.concat(this.vars, other.vars));
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


    public interface MapStr {
        public String apply(Var var);
    }

    public interface IMapStr {
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

    public interface MapNode {
        public Node apply(Var in);
    }

    public interface IMapNode {
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

    public interface MapEffect {
        public void apply(Var in);
    }

    public interface IMapEffect {
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
}