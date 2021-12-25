package src.SGFX;

import static org.lwjgl.opengl.GL43C.*;

public class BufFmt {
    public enum Type {
        U32(GL_UNSIGNED_INT),
        I32(GL_INT),
        F32(GL_FLOAT);
         
        public final int gl_value;
    
        private Type(int value) {
            gl_value = value;
        }
    }    

    public enum Usage {
        StreamRead(GL_STREAM_READ),
        StreamCopy(GL_STREAM_COPY),
        StreamDraw(GL_STREAM_DRAW),

        MutRead(GL_DYNAMIC_READ),
        MutCopy(GL_DYNAMIC_COPY),
        MutDraw(GL_DYNAMIC_DRAW),

        Read(GL_STATIC_READ),
        Copy(GL_STATIC_COPY),
        Draw(GL_STATIC_DRAW);

        public final int gl_value;

        private Usage(int value) {
            gl_value = value;
        }
    }

    public Type type;
    public int dim;
    public int padding;
    public int offset;
    public Usage usage;
    public boolean norm;

    public static BufFmt Block(Type type, int dim, Usage usage) {
        BufFmt fmt = new BufFmt();
        fmt.type = type;
        fmt.dim = dim;
        fmt.padding = 0;
        fmt.offset = 0;
        fmt.usage = usage;
        fmt.norm = false;
        return fmt;
    }
}
