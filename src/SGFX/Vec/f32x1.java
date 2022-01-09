package src.SGFX.Vec;
import src.SGFX.*;
import static org.lwjgl.opengl.GL43C.*;
// Generated class: Refer template/Main.java
public class f32x1 {
    public static class Arr {
        public float[] data;
        public Arr(int N) {
            data = new float[1 * N];
        }
        public int size() {
            return data.length / 1;
        }
        public static Arr of(float... data) {
            if (data.length % 1 != 0) {
                throw new RuntimeException("Data length must be multiple of vector dimension");
            }
            Arr out = new Arr(data.length);
            out.set_unaligned(data, 0, 0);
            return out;
        }
        public void resize(int N) {
            float[] new_data = new float[1 * N];
            System.arraycopy(data, 0, new_data, 0, Math.min(data.length, new_data.length));
            data = new_data;
        }
        public void set_unaligned(float[] other, int index, int offset) {
            System.arraycopy(other, 1 * index + offset, data, 0, other.length);
        }
        public void set_unaligned(Arr other, int index, int offset) {
            System.arraycopy(other.data, 1 * index + offset, data, 0, other.size());
        }
        public void set(int index, f32x1 vec) {
            data[1 * index + 0] = vec.x;
        }
        public void set(int index, float x) {
            data[1 * index + 0] = x;
        }
        public f32x1 get(int index) {
            return new f32x1(data[1 * index + 0]);
        }
    }
    public static class Buf extends GL_Buf {
        public Arr local;
        public Buf(int target, BufFmt.Usage usage, int reserve) {
            super(target, BufFmt.Block(BufFmt.Type.F32, 1, usage));
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
        public void write_unaligned(float[] data, int index, int offset) {
            bind();
            glBufferSubData(target, 1*index + offset, data);
            unbind();
        }
        public void write_unaligned(Arr data, int index, int offset) {
            write_unaligned(data.data, index, offset);
        }
        public void write_unaligned(f32x1 data, int index, int offset) {
            write_unaligned(data.as_array(), index, offset);
        }
        public void write(Arr data, int index) {
            write_unaligned(data.data, index, 0);
        }
        public void write(f32x1 data, int index) {
            write_unaligned(data.as_array(), index, 0);
        }
    }
    public float x;
    public f32x1(float x) {
        this.x = x;
    }
    public f32x1 copy() {
        return new f32x1(x);
    }
    public static f32x1 of(float x) {
        return new f32x1(x);
    }
    public String toString() {
        return "[" + x + "]";
    }
    public static f32x1 zero() {
        return of(0);
    }
    public static f32x1 val(float val) {
        return of(val);
    }
    public f32x1 set(float x) {
        this.x = x;
        return this;
    }
    public f32x1 set(f32x1 other) {
        this.x = other.x;
        return this;
    }
    public float[] as_array() {
        float[] data = { x };
        return data;
    }
    public f32x1 add(f32x1 other) {
        this.x += other.x;
        return this;
    }
    public f32x1 cadd(f32x1 other) {
        return this.copy().add(other);
    }
    public f32x1 add(float x) {
        this.x += x;
        return this;
    }
    public f32x1 cadd(float x) {
        return this.copy().add(x);
    }
    public f32x1 sub(f32x1 other) {
        this.x -= other.x;
        return this;
    }
    public f32x1 csub(f32x1 other) {
        return this.copy().sub(other);
    }
    public f32x1 sub(float x) {
        this.x -= x;
        return this;
    }
    public f32x1 csub(float x) {
        return this.copy().sub(x);
    }
    public f32x1 mul(f32x1 other) {
        this.x *= other.x;
        return this;
    }
    public f32x1 cmul(f32x1 other) {
        return this.copy().mul(other);
    }
    public f32x1 mul(float x) {
        this.x *= x;
        return this;
    }
    public f32x1 cmul(float x) {
        return this.copy().mul(x);
    }
    public f32x1 div(f32x1 other) {
        this.x /= other.x;
        return this;
    }
    public f32x1 cdiv(f32x1 other) {
        return this.copy().div(other);
    }
    public f32x1 div(float x) {
        this.x /= x;
        return this;
    }
    public f32x1 cdiv(float x) {
        return this.copy().div(x);
    }
    public f32x1 max(f32x1 other) {
        this.x = Math.max(this.x,other.x);
        return this;
    }
    public f32x1 cmax(f32x1 other) {
        return this.copy().max(other);
    }
    public f32x1 max(float x) {
        this.x = Math.max(this.x,x);
        return this;
    }
    public f32x1 cmax(float x) {
        return this.copy().max(x);
    }
    public f32x1 min(f32x1 other) {
        this.x = Math.min(this.x,other.x);
        return this;
    }
    public f32x1 cmin(f32x1 other) {
        return this.copy().min(other);
    }
    public f32x1 min(float x) {
        this.x = Math.min(this.x,x);
        return this;
    }
    public f32x1 cmin(float x) {
        return this.copy().min(x);
    }
    public f32x1 sadd(float other) {
        this.x += other;
        return this;
    }
    public f32x1 csadd(float other) {
        return this.copy().sadd(other);
    }
    public f32x1 ssub(float other) {
        this.x -= other;
        return this;
    }
    public f32x1 cssub(float other) {
        return this.copy().ssub(other);
    }
    public f32x1 smul(float other) {
        this.x *= other;
        return this;
    }
    public f32x1 csmul(float other) {
        return this.copy().smul(other);
    }
    public f32x1 sdiv(float other) {
        this.x /= other;
        return this;
    }
    public f32x1 csdiv(float other) {
        return this.copy().sdiv(other);
    }
    public f32x1 reset() {
        x = 0;
        return this;
    }
    public f32x1 reset(float value) {
        x = value;
        return this;
    }
    public float dot(f32x1 other) {
        return x*other.x;
    }
    public float dist(f32x1 other) {
        return this.csub(other).mag();
    }
    public float distSq(f32x1 other) {
        return this.csub(other).magSq();
    }
    public float dot(float x) {
        return x*x;
    }
    public float dist(float x) {
        return this.csub(x).mag();
    }
    public float distSq(float x) {
        return this.csub(x).magSq();
    }
    public f32x1 abs() {
        x = Math.abs(x);
        return this;
    }
    public f32x1 cabs() {
        return this.copy().abs();
    }
    public f32x1 inv() {
        x = 1/x;
        return this;
    }
    public f32x1 cinv() {
        return this.copy().inv();
    }
    public float sum() {
        return x;
    }
    public float prod() {
        return x;
    }
    public float magSq() {
        return this.dot(this);
    }
    public float mag() {
        return (float)Math.sqrt(this.dot(this));
    }
    public float max() {
        return x;
    }
    public float min() {
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
        glUniform1f(index, x);
    }
    public u32x1 u() {
        return new u32x1((int)x);
    }
    public i32x1 i() {
        return new i32x1((int)x);
    }
}