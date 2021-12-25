package src.SGFX;

import static org.lwjgl.opengl.GL20C.*;

public abstract class GL_Buf extends GL_Bindable implements Buf {
    public final int target;
    public final BufFmt fmt;

    public GL_Buf(int target, BufFmt fmt) {
        super(glGenBuffers());
        this.target = target;
        this.fmt = fmt;
    }

    public void destroy() {
        glDeleteBuffers(gl_handle);
    }

    protected void bind_handle(int handle) {
        glBindBuffer(target, handle);
    }

    public int getTarget() {
        return target;
    }

    public BufFmt getFmt() {
        return fmt;
    }

    abstract public int getLen();
}
