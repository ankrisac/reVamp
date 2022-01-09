package template.cons;

import java.util.ArrayList;

public class Block extends Node {
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

    public static Block Else(Node... inner) {
        return Block.of("else", inner);
    }

    public static Block While(String cond, Node... inner) {
        return Block.Cond("while", cond, inner);
    }
}