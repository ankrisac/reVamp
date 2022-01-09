package src.SGFX.Vec;
import src.SGFX.*;
import static org.lwjgl.opengl.GL43C.*;
// Generated class: Refer template/Main.java
public class f32x2 {
    public static class Arr {
        public float[] data;
        public Arr(int N) {
            data = new float[2 * N];
        }
        public int size() {
            return data.length / 2;
        }
        public static Arr of(float... data) {
            if (data.length % 2 != 0) {
                throw new RuntimeException("Data length must be multiple of vector dimension");
            }
            Arr out = new Arr(data.length);
            out.set_unaligned(data, 0, 0);
            return out;
        }
        public void resize(int N) {
            float[] new_data = new float[2 * N];
            System.arraycopy(data, 0, new_data, 0, Math.min(data.length, new_data.length));
            data = new_data;
        }
        public void set_unaligned(float[] other, int index, int offset) {
            System.arraycopy(other, 2 * index + offset, data, 0, other.length);
        }
        public void set_unaligned(Arr other, int index, int offset) {
            System.arraycopy(other.data, 2 * index + offset, data, 0, other.size());
        }
        public void set(int index, f32x2 vec) {
            data[2 * index + 0] = vec.x;
            data[2 * index + 1] = vec.y;
        }
        public void set(int index, float x, float y) {
            data[2 * index + 0] = x;
            data[2 * index + 1] = y;
        }
        public f32x2 get(int index) {
            return new f32x2(data[2 * index + 0], data[2 * index + 1]);
        }
    }
    public static class Buf extends GL_Buf {
        public Arr local;
        public Buf(int target, BufFmt.Usage usage, int reserve) {
            super(target, BufFmt.Block(BufFmt.Type.F32, 2, usage));
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
            glBufferSubData(target, 2*index + offset, data);
            unbind();
        }
        public void write_unaligned(Arr data, int index, int offset) {
            write_unaligned(data.data, index, offset);
        }
        public void write_unaligned(f32x2 data, int index, int offset) {
            write_unaligned(data.as_array(), index, offset);
        }
        public void write(Arr data, int index) {
            write_unaligned(data.data, index, 0);
        }
        public void write(f32x2 data, int index) {
            write_unaligned(data.as_array(), index, 0);
        }
    }
    public float x;
    public float y;
    public f32x2(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public f32x2 copy() {
        return new f32x2(x, y);
    }
    public static f32x2 of(float x, float y) {
        return new f32x2(x, y);
    }
    public String toString() {
        return "[" + x + "," + y + "]";
    }
    public static f32x2 zero() {
        return of(0, 0);
    }
    public static f32x2 val(float val) {
        return of(val, val);
    }
    public f32x2 set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }
    public f32x2 set(f32x2 other) {
        this.x = other.x;
        this.y = other.y;
        return this;
    }
    public float[] as_array() {
        float[] data = { x, y };
        return data;
    }
    public f32x2 add(f32x2 other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }
    public f32x2 cadd(f32x2 other) {
        return this.copy().add(other);
    }
    public f32x2 add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }
    public f32x2 cadd(float x, float y) {
        return this.copy().add(x, y);
    }
    public f32x2 sub(f32x2 other) {
        this.x -= other.x;
        this.y -= other.y;
        return this;
    }
    public f32x2 csub(f32x2 other) {
        return this.copy().sub(other);
    }
    public f32x2 sub(float x, float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }
    public f32x2 csub(float x, float y) {
        return this.copy().sub(x, y);
    }
    public f32x2 mul(f32x2 other) {
        this.x *= other.x;
        this.y *= other.y;
        return this;
    }
    public f32x2 cmul(f32x2 other) {
        return this.copy().mul(other);
    }
    public f32x2 mul(float x, float y) {
        this.x *= x;
        this.y *= y;
        return this;
    }
    public f32x2 cmul(float x, float y) {
        return this.copy().mul(x, y);
    }
    public f32x2 div(f32x2 other) {
        this.x /= other.x;
        this.y /= other.y;
        return this;
    }
    public f32x2 cdiv(f32x2 other) {
        return this.copy().div(other);
    }
    public f32x2 div(float x, float y) {
        this.x /= x;
        this.y /= y;
        return this;
    }
    public f32x2 cdiv(float x, float y) {
        return this.copy().div(x, y);
    }
    public f32x2 max(f32x2 other) {
        this.x = Math.max(this.x,other.x);
        this.y = Math.max(this.y,other.y);
        return this;
    }
    public f32x2 cmax(f32x2 other) {
        return this.copy().max(other);
    }
    public f32x2 max(float x, float y) {
        this.x = Math.max(this.x,x);
        this.y = Math.max(this.y,y);
        return this;
    }
    public f32x2 cmax(float x, float y) {
        return this.copy().max(x, y);
    }
    public f32x2 min(f32x2 other) {
        this.x = Math.min(this.x,other.x);
        this.y = Math.min(this.y,other.y);
        return this;
    }
    public f32x2 cmin(f32x2 other) {
        return this.copy().min(other);
    }
    public f32x2 min(float x, float y) {
        this.x = Math.min(this.x,x);
        this.y = Math.min(this.y,y);
        return this;
    }
    public f32x2 cmin(float x, float y) {
        return this.copy().min(x, y);
    }
    public f32x2 sadd(float other) {
        this.x += other;
        this.y += other;
        return this;
    }
    public f32x2 csadd(float other) {
        return this.copy().sadd(other);
    }
    public f32x2 ssub(float other) {
        this.x -= other;
        this.y -= other;
        return this;
    }
    public f32x2 cssub(float other) {
        return this.copy().ssub(other);
    }
    public f32x2 smul(float other) {
        this.x *= other;
        this.y *= other;
        return this;
    }
    public f32x2 csmul(float other) {
        return this.copy().smul(other);
    }
    public f32x2 sdiv(float other) {
        this.x /= other;
        this.y /= other;
        return this;
    }
    public f32x2 csdiv(float other) {
        return this.copy().sdiv(other);
    }
    public f32x2 reset() {
        x = 0;
        y = 0;
        return this;
    }
    public f32x2 reset(float value) {
        x = value;
        y = value;
        return this;
    }
    public float dot(f32x2 other) {
        return x*other.x + y*other.y;
    }
    public float dist(f32x2 other) {
        return this.csub(other).mag();
    }
    public float distSq(f32x2 other) {
        return this.csub(other).magSq();
    }
    public float dot(float x, float y) {
        return x*x + y*y;
    }
    public float dist(float x, float y) {
        return this.csub(x, y).mag();
    }
    public float distSq(float x, float y) {
        return this.csub(x, y).magSq();
    }
    public f32x2 abs() {
        x = Math.abs(x);
        y = Math.abs(y);
        return this;
    }
    public f32x2 cabs() {
        return this.copy().abs();
    }
    public f32x2 inv() {
        x = 1/x;
        y = 1/y;
        return this;
    }
    public f32x2 cinv() {
        return this.copy().inv();
    }
    public float sum() {
        return x + y;
    }
    public float prod() {
        return x * y;
    }
    public float magSq() {
        return this.dot(this);
    }
    public float mag() {
        return (float)Math.sqrt(this.dot(this));
    }
    public float max() {
        return Math.max(x,y);
    }
    public float min() {
        return Math.min(x,y);
    }
    public float grad() {
        return y / x;
    }
    public float invgrad() {
        return x / y;
    }
    public f32x2 Jconj() {
        y = -y;
        return this;
    }
    public f32x2 cJconj() {
        return this.copy().Jconj();
    }
    public f32x2 Jinv() {
        float mag = x * x + y * y;
        x /= mag;
        y /= mag;
        return this;
    }
    public f32x2 cJinv() {
        return this.copy().Jinv();
    }
    public f32x2 Jmul(f32x2 other) {
        float a = x * other.x + y * other.y;
        float b = x * other.y - y * other.x;
        x = a;
        y = b;
        return this;
    }
    public f32x2 cJmul(f32x2 other) {
        return this.copy().Jmul(other);
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
        glUniform2f(index, x, y);
    }
    public u32x2 u() {
        return new u32x2((int)x, (int)y);
    }
    public i32x2 i() {
        return new i32x2((int)x, (int)y);
    }
}