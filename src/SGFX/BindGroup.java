package src.SGFX;
import static org.lwjgl.opengl.GL31C.*;

public class BindGroup extends GL_Bindable {
    public enum DrawMode {
        Tris(GL_TRIANGLES),
        TrisStrip(GL_TRIANGLE_STRIP);
    
        public final int gl_value;
    
        private DrawMode(int value) {
            gl_value = value;
        }
    }

    public BindGroup() {
        super(glGenVertexArrays());
    }

    public void destroy() {
        glDeleteVertexArrays(gl_handle);
    }

    protected void bind_handle(int handle) {
        glBindVertexArray(handle);
    }

    public void attach(int index, Buf.Attrib attrib) {
        bind();
        attrib.bind();
        {
            BufFmt fmt = attrib.getFmt();

            switch (fmt.type) {
                case U32:
                case I32:
                    glVertexAttribIPointer(index, fmt.dim, fmt.type.gl_value, fmt.padding, fmt.offset);
                    break;
                default:
                    glVertexAttribPointer(index, fmt.dim, fmt.type.gl_value, fmt.norm, fmt.padding, fmt.offset);
                    break;
            }
            glEnableVertexAttribArray(index);
        }
        attrib.unbind();
        unbind();

        glDisableVertexAttribArray(0);
    }

    public void draw(DrawMode mode, int offset, int len) {
        bind();
        glDrawArrays(mode.gl_value, offset, len);
        unbind();
    }

    public void draw(DrawMode mode, Buf.Index ibo, int len) {
        bind();
        ibo.bind();
        glDrawElements(mode.gl_value, len, ibo.getFmt().type.gl_value, 0);
        ibo.unbind();
        unbind();
    }

    public void draw(DrawMode mode, Buf.Index ibo, int len, int instances) {
        bind();
        ibo.bind();
        glDrawElementsInstanced(mode.gl_value, len, ibo.getFmt().type.gl_value, 0, instances);
        ibo.unbind();
        unbind();
    }
}
