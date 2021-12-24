package template.cons;

public class Struct extends Node {
    public static class Member extends Node {
        boolean m_public;
        boolean m_static;
        boolean m_final;
        boolean m_abstract;
        Node node;

        public Member(Var var) {
            this.node = Ln.of(var.def());
            m_public = true;
            m_static = false;
            m_final = false;
            m_abstract = false;
        }

        public Member(Node node) {
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

    public Struct(String name) {
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

    public void add_class(Struct _class) {
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