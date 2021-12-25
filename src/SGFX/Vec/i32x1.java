package src.SGFX.Vec;
import src.SGFX.*;
import static org.lwjgl.opengl.GL43C.*;
// Generated class: Refer template/Main.java
public class i32x1 {
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
        public void set(int index, i32x1 vec) {
            data[1 * index + 0] = vec.x;
        }
        public void set(int index, int x) {
            data[1 * index + 0] = x;
        }
        public i32x1 get(int index) {
            return new i32x1(data[1 * index + 0]);
        }
    }
    public static class Buf extends GL_Buf {
        public final Arr local;
        public Buf(int target, BufFmt.Usage usage, int reserve) {
            super(target, BufFmt.Block(BufFmt.Type.I32, 1, usage));
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
        public void update_range_unaligned(i32x1 data, int index, int offset) {
            update_range_unaligned(data.as_array(), index, offset);
        }
        public void update_range(Arr data, int index) {
            update_range_unaligned(data.data, index, 0);
        }
        public void update_range(i32x1 data, int index) {
            update_range_unaligned(data.as_array(), index, 0);
        }
    }
    public int x;
    public i32x1(int x) {
        this.x = x;
    }
    public static i32x1 of(int x) {
        return new i32x1(x);
    }
    public i32x1 copy() {
        return new i32x1(x);
    }
    public int[] as_array() {
        int[] data = { x };
        return data;
    }
    public i32x1 add(i32x1 other) {
        this.x += other.x;
        return this;
    }
    public i32x1 cadd(i32x1 other) {
        return this.copy().add(other);
    }
    public i32x1 add(int x) {
        this.x += x;
        return this;
    }
    public i32x1 cadd(int x) {
        return this.copy().add(x);
    }
    public i32x1 sub(i32x1 other) {
        this.x -= other.x;
        return this;
    }
    public i32x1 csub(i32x1 other) {
        return this.copy().sub(other);
    }
    public i32x1 sub(int x) {
        this.x -= x;
        return this;
    }
    public i32x1 csub(int x) {
        return this.copy().sub(x);
    }
    public i32x1 mul(i32x1 other) {
        this.x *= other.x;
        return this;
    }
    public i32x1 cmul(i32x1 other) {
        return this.copy().mul(other);
    }
    public i32x1 mul(int x) {
        this.x *= x;
        return this;
    }
    public i32x1 cmul(int x) {
        return this.copy().mul(x);
    }
    public i32x1 div(i32x1 other) {
        this.x /= other.x;
        return this;
    }
    public i32x1 cdiv(i32x1 other) {
        return this.copy().div(other);
    }
    public i32x1 div(int x) {
        this.x /= x;
        return this;
    }
    public i32x1 cdiv(int x) {
        return this.copy().div(x);
    }
    public i32x1 reset() {
        x = 0;
        return this;
    }
    public i32x1 creset() {
        return this.copy().reset();
    }
    public i32x1 reset(int value) {
        x = value;
        return this;
    }
    public i32x1 creset(int value) {
        return this.copy().reset(value);
    }
    public int dot(i32x1 other) {
        return x*other.x;
    }
    public int dist(i32x1 other) {
        return this.csub(other).mag();
    }
    public int distSq(i32x1 other) {
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
    public i32x1 abs() {
        x = Math.abs(x);
        return this;
    }
    public i32x1 cabs() {
        return this.copy().abs();
    }
    public i32x1 inv() {
        x = 1/x;
        return this;
    }
    public i32x1 cinv() {
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
    public void bind(int index) {
        glUniform1i(index, x);
    }
    public u32x1 u() {
        return new u32x1((int)x);
    }
    public f32x1 f() {
        return new f32x1((float)x);
    }
}