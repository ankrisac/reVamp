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
        public static Arr of(int... data) {
            if (data.length % 1 != 0) {
                throw new RuntimeException("Data length must be multiple of vector dimension");
            }
            Arr out = new Arr(data.length);
            out.set_unaligned(data, 0, 0);
            return out;
        }
        public void resize(int N) {
            int[] new_data = new int[1 * N];
            System.arraycopy(data, 0, new_data, 0, Math.min(data.length, new_data.length));
            data = new_data;
        }
        public void set_unaligned(int[] other, int index, int offset) {
            System.arraycopy(other, 1 * index + offset, data, 0, other.length);
        }
        public void set_unaligned(Arr other, int index, int offset) {
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
        public Arr local;
        public Buf(int target, BufFmt.Usage usage, int reserve) {
            super(target, BufFmt.Block(BufFmt.Type.U32, 1, usage));
            local = new Arr(reserve);
        }
        public void update(boolean realloc) {
            if (realloc) {
                bind();
                glBufferData(target, local.data, fmt.usage.gl_value);
                unbind();
            }
            else {
                write(local, 0);
            }
        }
        public void write_unaligned(int[] data, int index, int offset) {
            bind();
            glBufferSubData(target, 1*index + offset, data);
            unbind();
        }
        public void write_unaligned(Arr data, int index, int offset) {
            write_unaligned(data.data, index, offset);
        }
        public void write_unaligned(u32x1 data, int index, int offset) {
            write_unaligned(data.as_array(), index, offset);
        }
        public void write(Arr data, int index) {
            write_unaligned(data.data, index, 0);
        }
        public void write(u32x1 data, int index) {
            write_unaligned(data.as_array(), index, 0);
        }
    }
    public int x;
    public u32x1(int x) {
        this.x = x;
    }
    public u32x1 copy() {
        return new u32x1(x);
    }
    public static u32x1 of(int x) {
        return new u32x1(x);
    }
    public String toString() {
        return "[" + x + "]";
    }
    public static u32x1 zero() {
        return of(0);
    }
    public static u32x1 val(int val) {
        return of(val);
    }
    public u32x1 set(int x) {
        this.x = x;
        return this;
    }
    public u32x1 set(u32x1 other) {
        this.x = other.x;
        return this;
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
    public u32x1 max(u32x1 other) {
        this.x = Math.max(this.x,other.x);
        return this;
    }
    public u32x1 cmax(u32x1 other) {
        return this.copy().max(other);
    }
    public u32x1 max(int x) {
        this.x = Math.max(this.x,x);
        return this;
    }
    public u32x1 cmax(int x) {
        return this.copy().max(x);
    }
    public u32x1 min(u32x1 other) {
        this.x = Math.min(this.x,other.x);
        return this;
    }
    public u32x1 cmin(u32x1 other) {
        return this.copy().min(other);
    }
    public u32x1 min(int x) {
        this.x = Math.min(this.x,x);
        return this;
    }
    public u32x1 cmin(int x) {
        return this.copy().min(x);
    }
    public u32x1 sadd(int other) {
        this.x += other;
        return this;
    }
    public u32x1 csadd(int other) {
        return this.copy().sadd(other);
    }
    public u32x1 ssub(int other) {
        this.x -= other;
        return this;
    }
    public u32x1 cssub(int other) {
        return this.copy().ssub(other);
    }
    public u32x1 smul(int other) {
        this.x *= other;
        return this;
    }
    public u32x1 csmul(int other) {
        return this.copy().smul(other);
    }
    public u32x1 sdiv(int other) {
        this.x /= other;
        return this;
    }
    public u32x1 csdiv(int other) {
        return this.copy().sdiv(other);
    }
    public u32x1 reset() {
        x = 0;
        return this;
    }
    public u32x1 reset(int value) {
        x = value;
        return this;
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