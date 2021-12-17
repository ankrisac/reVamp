package src.SGFX;

import static org.lwjgl.opengl.GL43C.*;

public class BufFmt {
    public enum Type {
        U8(GL_UNSIGNED_BYTE),
        U16(GL_UNSIGNED_SHORT),
        U32(GL_UNSIGNED_INT),
    
        I8(GL_BYTE),
        I16(GL_SHORT),
        I32(GL_INT),
    
        F16(GL_HALF_FLOAT),
        F32(GL_FLOAT),
        F64(GL_DOUBLE);
    
        public final int gl_value;
    
        private Type(int value) {
            gl_value = value;
        }
    
        public void expect(Type... types) {
            for (Type type : types) {
                if (this == type) {
                    return;
                }
            }
    
            String err = "";
            for (Type type : types) {
                err += type.toString() + ", ";
            }
            throw new RuntimeException("Expected " + err);
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
