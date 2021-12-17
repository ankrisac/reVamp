package src.SGFX;

import static org.lwjgl.opengl.GL43C.*;

public class Buf extends BufRaw {
    public Buf(int target, BufFmt fmt) {
        super(target, fmt);
    }

    public void load(int data[]) {
        raw_load(data.length, () -> {
            glBufferData(target, data, fmt.usage.gl_value);
        }, BufFmt.Type.U32, BufFmt.Type.I32);
    }

    public void subload(int offset, int data[]) {
        raw_subload(() -> {
            glBufferSubData(target, offset, data);
        }, BufFmt.Type.U32, BufFmt.Type.I32);
    }

    public void load(float data[]) {
        raw_load(data.length, () -> {
            glBufferData(target, data, fmt.usage.gl_value);
        }, BufFmt.Type.F32);
    }

    public void subload(int offset, float data[]) {
        raw_subload(() -> {
            glBufferSubData(target, offset, data);
        }, BufFmt.Type.F32);
    }

    public void load(double data[]) {
        raw_load(data.length, () -> {
            glBufferData(target, data, fmt.usage.gl_value);
        }, BufFmt.Type.F64);
    }

    public void subload(int offset, double data[]) {
        raw_subload(() -> {
            glBufferSubData(target, offset, data);
        }, BufFmt.Type.F64);
    }
}
