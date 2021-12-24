package template.cons;

public class Ln extends Node {
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