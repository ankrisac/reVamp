package src.SGFX;

public class SGFX_Error extends RuntimeException {
    public SGFX_Error(String label, String type, String err, String infolog) {
        super(type + "[" + label + "]: " + err + "\n" + infolog);
    }
}
