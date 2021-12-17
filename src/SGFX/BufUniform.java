package src.SGFX;
import static org.lwjgl.opengl.GL43C.*;

public class BufUniform extends Buf {
    public BufUniform(BufFmt fmt) {
        super(GL_UNIFORM_BUFFER, fmt);
    }
}
