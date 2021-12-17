package src.SGFX;

import static org.lwjgl.opengl.GL43C.*;

public abstract class BufRaw extends GL_Bindable {
    protected int len;

    public final int target;
    public final BufFmt fmt;

    public BufRaw(int target, BufFmt fmt) {
        super(glGenBuffers());
        this.target = target;
        this.fmt = fmt;
        this.len = 0;
    }

    public void destroy() {
        glDeleteBuffers(handle);
    }

    protected void bind_handle(int handle) {
        glBindBuffer(target, handle);
    }

    public int getLen() {
        return this.len;
    }

    public interface LoadFn {
        void load();
    }

    public <T> void raw_load(int length, LoadFn fn, BufFmt.Type... type) {
        fmt.type.expect(type);
        bind();
        len = length;
        fn.load();
        unbind();
    }

    public <T> void raw_subload(LoadFn fn, BufFmt.Type... type) {
        fmt.type.expect(type);
        bind();
        fn.load();
        unbind();
    }
}
