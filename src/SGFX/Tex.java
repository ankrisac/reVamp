package src.SGFX;

import static org.lwjgl.opengl.GL43C.*;

public class Tex extends GL_Bindable {
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

    public Tex(Dim dim) {
        super(glGenTextures());
        this.dim = dim;
    }

    public void destroy() {
        glDeleteTextures(handle);
    }

    protected void bind_handle(int handle) {
        glBindTexture(dim.gl_value, handle);
    }

    public void setParam(int param, int value) {
        glTexParameteri(dim.gl_value, param, value);
    }
}
