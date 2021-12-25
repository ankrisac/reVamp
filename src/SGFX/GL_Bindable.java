package src.SGFX;

public abstract class GL_Bindable extends GL_Object implements Bindable {
    public GL_Bindable(int handle) {
        super(handle);
    }

    abstract protected void bind_handle(int handle);

    final public void bind() {
        bind_handle(gl_handle);
    }

    final public void unbind() {
        bind_handle(0);
    }
}
