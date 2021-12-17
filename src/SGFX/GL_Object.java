package src.SGFX;

public abstract class GL_Object implements Resource {
    protected final int handle;

    public GL_Object(int handle) {
        this.handle = handle;
    }

    abstract public void destroy();
}        
