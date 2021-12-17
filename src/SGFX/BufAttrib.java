package src.SGFX;
import static org.lwjgl.opengl.GL43C.*;

public class BufAttrib extends Buf {
    public BufAttrib(BufFmt fmt) {
        super(GL_ARRAY_BUFFER, fmt);
    }
}

