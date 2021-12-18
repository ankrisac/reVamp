package src.SGFX;
import static org.lwjgl.opengl.GL43C.*;

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
        glDeleteVertexArrays(handle);
    }

    protected void bind_handle(int handle) {
        glBindVertexArray(handle);
    }

    public void attach(int index, BufAttrib attrib) {
        bind();
        attrib.bind();
        {
            BufFmt fmt = attrib.fmt;

            switch (fmt.type) {
                case U8:
                case U16:
                case U32:
                case I8:
                case I16:
                case I32:
                    glVertexAttribIPointer(index, fmt.dim, fmt.type.gl_value, fmt.padding, fmt.offset);
                    break;
                case F64:
                    glVertexAttribLPointer(index, fmt.dim, fmt.type.gl_value, fmt.padding, fmt.offset);
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

    public void draw(DrawMode mode, int begin, int len) {
        bind();
        glDrawArrays(mode.gl_value, begin, len);
        unbind();
    }

    public void draw(DrawMode mode, BufIndex index) {
        bind();
        index.bind();
        glDrawElements(mode.gl_value, index.getLen(), index.fmt.type.gl_value, 0);
        index.unbind();
        unbind();
    }

    public void draw(DrawMode mode, BufIndex index, int instances) {
        bind();
        index.bind();
        glDrawElementsInstanced(mode.gl_value, index.getLen(), index.fmt.type.gl_value, 0, instances);
        index.unbind();
        unbind();
    }
}
