package src.SGFX.Vec;
import src.SGFX.*;
import static org.lwjgl.opengl.GL43C.*;
// Generated class: Refer template/Main.java
public class u32x1 {
    public static class Arr {
        public int[] data;
        public Arr(int N) {
            data = new int[1 * N];
        }
        public int size() {
            return data.length / 1;
        }
        public void resize(int N) {
            int[] new_data = new int[1 * N];
            System.arraycopy(data, 0, new_data, 0, Math.min(data.length, new_data.length));
            data = new_data;
        }
        public void set(int[] other, int index, int offset) {
            System.arraycopy(other, 1 * index + offset, data, 0, other.length);
        }
        public void set(Arr other, int index, int offset) {
            System.arraycopy(other.data, 1 * index + offset, data, 0, other.size());
        }
        public void set(int index, u32x1 vec) {
            data[1 * index + 0] = vec.x;
        }
        public void set(int index, int x) {
            data[1 * index + 0] = x;
        }
        public u32x1 get(int index) {
            return new u32x1(data[1 * index + 0]);
        }
    }
    public static class Buf extends GL_Buf {
        public final Arr local;
        public Buf(int target, BufFmt.Usage usage, int reserve) {
            super(target, BufFmt.Block(BufFmt.Type.U32, 1, usage));
            local = new Arr(reserve);
        }
        public int getLen() {
            return local.size();
        }
        public void update() {
            bind();
            glBufferData(target, local.data, fmt.usage.gl_value);
            unbind();
        }
        public void update_range_unaligned(int[] data, int index, int offset) {
            bind();
            local.set(data, index, offset);
            glBufferSubData(target, 1*index + offset, data);
            unbind();
        }
        public void update_range_unaligned(Arr data, int index, int offset) {
            update_range_unaligned(data.data, index, offset);
        }
        public void update_range_unaligned(u32x1 data, int index, int offset) {
            update_range_unaligned(data.as_array(), index, offset);
        }
        public void update_range(Arr data, int index) {
            update_range_unaligned(data.data, index, 0);
        }
        public void update_range(u32x1 data, int index) {
            update_range_unaligned(data.as_array(), index, 0);
        }
    }
    public int x;
    public u32x1(int x) {
        this.x = x;
    }
    public static u32x1 of(int x) {
        return new u32x1(x);
    }
    public u32x1 copy() {
        return new u32x1(x);
    }
    public int[] as_array() {
        int[] data = { x };
        return data;
    }
    public u32x1 add(u32x1 other) {
        this.x += other.x;
        return this;
    }
    public u32x1 cadd(u32x1 other) {
        return this.copy().add(other);
    }
    public u32x1 add(int x) {
        this.x += x;
        return this;
    }
    public u32x1 cadd(int x) {
        return this.copy().add(x);
    }
    public u32x1 sub(u32x1 other) {
        this.x -= other.x;
        return this;
    }
    public u32x1 csub(u32x1 other) {
        return this.copy().sub(other);
    }
    public u32x1 sub(int x) {
        this.x -= x;
        return this;
    }
    public u32x1 csub(int x) {
        return this.copy().sub(x);
    }
    public u32x1 mul(u32x1 other) {
        this.x *= other.x;
        return this;
    }
    public u32x1 cmul(u32x1 other) {
        return this.copy().mul(other);
    }
    public u32x1 mul(int x) {
        this.x *= x;
        return this;
    }
    public u32x1 cmul(int x) {
        return this.copy().mul(x);
    }
    public u32x1 div(u32x1 other) {
        this.x /= other.x;
        return this;
    }
    public u32x1 cdiv(u32x1 other) {
        return this.copy().div(other);
    }
    public u32x1 div(int x) {
        this.x /= x;
        return this;
    }
    public u32x1 cdiv(int x) {
        return this.copy().div(x);
    }
    public u32x1 reset() {
        x = 0;
        return this;
    }
    public u32x1 creset() {
        return this.copy().reset();
    }
    public u32x1 reset(int value) {
        x = value;
        return this;
    }
    public u32x1 creset(int value) {
        return this.copy().reset(value);
    }
    public int dot(u32x1 other) {
        return x*other.x;
    }
    public int dist(u32x1 other) {
        return this.csub(other).mag();
    }
    public int distSq(u32x1 other) {
        return this.csub(other).magSq();
    }
    public int dot(int x) {
        return x*x;
    }
    public int dist(int x) {
        return this.csub(x).mag();
    }
    public int distSq(int x) {
        return this.csub(x).magSq();
    }
    public u32x1 abs() {
        x = Math.abs(x);
        return this;
    }
    public u32x1 cabs() {
        return this.copy().abs();
    }
    public u32x1 inv() {
        x = 1/x;
        return this;
    }
    public u32x1 cinv() {
        return this.copy().inv();
    }
    public int sum() {
        return x;
    }
    public int prod() {
        return x;
    }
    public int magSq() {
        return this.dot(this);
    }
    public int mag() {
        return (int)Math.sqrt(this.dot(this));
    }
    public int max() {
        return x;
    }
    public int min() {
        return x;
    }
    public static class Storage extends Buf implements Buf.Storage {
        public Storage(BufFmt.Usage usage, int reserve) {
            super(GL_SHADER_STORAGE_BUFFER, usage, reserve);
        }
        public void set_binding(int binding) {
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, binding, gl_handle);
        }
    }
    public static class Uniform extends Buf implements Buf.Uniform {
        public Uniform(BufFmt.Usage usage, int reserve) {
            super(GL_UNIFORM_BUFFER, usage, reserve);
        }
        public void set_binding(int binding) {
            glBindBufferBase(GL_UNIFORM_BUFFER, binding, gl_handle);
        }
    }
    public static class Attrib extends Buf implements Buf.Attrib {
        public Attrib(BufFmt.Usage usage, int reserve) {
            super(GL_ARRAY_BUFFER, usage, reserve);
        }
    }
    public static class Index extends Buf implements Buf.Index {
        public Index(BufFmt.Usage usage, int reserve) {
            super(GL_ELEMENT_ARRAY_BUFFER, usage, reserve);
        }
    }
    public void bind(int index) {
        glUniform1ui(index, x);
    }
    public i32x1 i() {
        return new i32x1((int)x);
    }
    public f32x1 f() {
        return new f32x1((float)x);
    }
}