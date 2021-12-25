package src.SGFX;

public abstract class GL_Object implements Resource {
    public final int gl_handle;

    public GL_Object(int handle) {
        this.gl_handle = handle;
    }

    abstract public void destroy();
}        
