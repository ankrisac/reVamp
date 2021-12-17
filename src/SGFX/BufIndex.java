package src.SGFX;
import static org.lwjgl.opengl.GL43C.*;

public class BufIndex extends BufRaw {
    public BufIndex(BufFmt fmt) {
        super(GL_ELEMENT_ARRAY_BUFFER, fmt);
        fmt.type.expect(BufFmt.Type.U8, BufFmt.Type.U16, BufFmt.Type.U32);
    }

    public void load(short data[]) {
        raw_load(data.length, () -> {
            glBufferData(target, data, fmt.usage.gl_value);
        }, BufFmt.Type.U16);
    }

    public void load(int data[]) {
        raw_load(data.length, () -> {
            glBufferData(target, data, fmt.usage.gl_value);
        }, BufFmt.Type.U32);
    }
}
