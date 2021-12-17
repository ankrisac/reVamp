package src.SGFX;

import static org.lwjgl.opengl.GL43C.*;

public class TexWrap {
    public enum Axis {
        Repeat(GL_REPEAT),
        RepeatMirror(GL_MIRRORED_REPEAT),
        ClampBorder(GL_CLAMP_TO_BORDER),
        ClampEdge(GL_CLAMP_TO_EDGE);

        public final int gl_value;

        private Axis(int value) {
            gl_value = value;
        }
    }

    public final Axis r;
    public final Axis s;
    public final Axis t;

    public TexWrap(Axis r, Axis s, Axis t) {
        this.r = r;
        this.s = s;
        this.t = t;
    }

    public TexWrap(Axis s, Axis t) {
        this.r = null;
        this.s = s;
        this.t = t;
    }
}
