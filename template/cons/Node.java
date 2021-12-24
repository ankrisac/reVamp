package template.cons;

public abstract class Node {
    public static String tab = "    ";
    public static String newline = "\n";

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