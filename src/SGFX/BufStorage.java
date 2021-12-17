package src.SGFX;

import static org.lwjgl.opengl.GL43C.*;

public class BufStorage extends Buf {
    public BufStorage(BufFmt fmt) {
        super(GL_SHADER_STORAGE_BUFFER, fmt);
    }

    public void set_binding(int binding) {
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, binding, handle);
    }
}
