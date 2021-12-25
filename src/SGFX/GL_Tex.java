package src.SGFX;

import static org.lwjgl.opengl.GL12C.*;

public class GL_Tex extends GL_Bindable {
    public enum Dim {
        D1(GL_TEXTURE_1D),
        D2(GL_TEXTURE_2D),
        D3(GL_TEXTURE_3D);
    
        public final int gl_value;
    
        private Dim(int value) {
            gl_value = value;
        }
    }
    
    public final Dim dim;

    public GL_Tex(Dim dim) {
        super(glGenTextures());
        this.dim = dim;
    }

    public void destroy() {
        glDeleteTextures(gl_handle);
    }

    protected void bind_handle(int handle) {
        glBindTexture(dim.gl_value, handle);
    }

    public void setParam(int param, int value) {
        glTexParameteri(dim.gl_value, param, value);
    }
}
