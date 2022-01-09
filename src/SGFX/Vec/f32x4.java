package src.SGFX.Vec;
import src.SGFX.*;
import static org.lwjgl.opengl.GL43C.*;
// Generated class: Refer template/Main.java
public class f32x4 {
    public static class Arr {
        public float[] data;
        public Arr(int N) {
            data = new float[4 * N];
        }
        public int size() {
            return data.length / 4;
        }
        public static Arr of(float... data) {
            if (data.length % 4 != 0) {
                throw new RuntimeException("Data length must be multiple of vector dimension");
            }
            Arr out = new Arr(data.length);
            out.set_unaligned(data, 0, 0);
            return out;
        }
        public void resize(int N) {
            float[] new_data = new float[4 * N];
            System.arraycopy(data, 0, new_data, 0, Math.min(data.length, new_data.length));
            data = new_data;
        }
        public void set_unaligned(float[] other, int index, int offset) {
            System.arraycopy(other, 4 * index + offset, data, 0, other.length);
        }
        public void set_unaligned(Arr other, int index, int offset) {
            System.arraycopy(other.data, 4 * index + offset, data, 0, other.size());
        }
        public void set(int index, f32x4 vec) {
            data[4 * index + 0] = vec.x;
            data[4 * index + 1] = vec.y;
            data[4 * index + 2] = vec.z;
            data[4 * index + 3] = vec.w;
        }
        public void set(int index, float x, float y, float z, float w) {
            data[4 * index + 0] = x;
            data[4 * index + 1] = y;
            data[4 * index + 2] = z;
            data[4 * index + 3] = w;
        }
        public f32x4 get(int index) {
            return new f32x4(data[4 * index + 0], data[4 * index + 1], data[4 * index + 2], data[4 * index + 3]);
        }
    }
    public static class Buf extends GL_Buf {
        public Arr local;
        public Buf(int target, BufFmt.Usage usage, int reserve) {
            super(target, BufFmt.Block(BufFmt.Type.F32, 4, usage));
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
            glBufferSubData(target, 4*index + offset, data);
            unbind();
        }
        public void write_unaligned(Arr data, int index, int offset) {
            write_unaligned(data.data, index, offset);
        }
        public void write_unaligned(f32x4 data, int index, int offset) {
            write_unaligned(data.as_array(), index, offset);
        }
        public void write(Arr data, int index) {
            write_unaligned(data.data, index, 0);
        }
        public void write(f32x4 data, int index) {
            write_unaligned(data.as_array(), index, 0);
        }
    }
    public float x;
    public float y;
    public float z;
    public float w;
    public f32x4(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    public f32x4 copy() {
        return new f32x4(x, y, z, w);
    }
    public static f32x4 of(float x, float y, float z, float w) {
        return new f32x4(x, y, z, w);
    }
    public String toString() {
        return "[" + x + "," + y + "," + z + "," + w + "]";
    }
    public static f32x4 zero() {
        return of(0, 0, 0, 0);
    }
    public static f32x4 val(float val) {
        return of(val, val, val, val);
    }
    public f32x4 set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }
    public f32x4 set(f32x4 other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.w = other.w;
        return this;
    }
    public float[] as_array() {
        float[] data = { x, y, z, w };
        return data;
    }
    public f32x4 add(f32x4 other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        this.w += other.w;
        return this;
    }
    public f32x4 cadd(f32x4 other) {
        return this.copy().add(other);
    }
    public f32x4 add(float x, float y, float z, float w) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.w += w;
        return this;
    }
    public f32x4 cadd(float x, float y, float z, float w) {
        return this.copy().add(x, y, z, w);
    }
    public f32x4 sub(f32x4 other) {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        this.w -= other.w;
        return this;
    }
    public f32x4 csub(f32x4 other) {
        return this.copy().sub(other);
    }
    public f32x4 sub(float x, float y, float z, float w) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        this.w -= w;
        return this;
    }
    public f32x4 csub(float x, float y, float z, float w) {
        return this.copy().sub(x, y, z, w);
    }
    public f32x4 mul(f32x4 other) {
        this.x *= other.x;
        this.y *= other.y;
        this.z *= other.z;
        this.w *= other.w;
        return this;
    }
    public f32x4 cmul(f32x4 other) {
        return this.copy().mul(other);
    }
    public f32x4 mul(float x, float y, float z, float w) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        this.w *= w;
        return this;
    }
    public f32x4 cmul(float x, float y, float z, float w) {
        return this.copy().mul(x, y, z, w);
    }
    public f32x4 div(f32x4 other) {
        this.x /= other.x;
        this.y /= other.y;
        this.z /= other.z;
        this.w /= other.w;
        return this;
    }
    public f32x4 cdiv(f32x4 other) {
        return this.copy().div(other);
    }
    public f32x4 div(float x, float y, float z, float w) {
        this.x /= x;
        this.y /= y;
        this.z /= z;
        this.w /= w;
        return this;
    }
    public f32x4 cdiv(float x, float y, float z, float w) {
        return this.copy().div(x, y, z, w);
    }
    public f32x4 max(f32x4 other) {
        this.x = Math.max(this.x,other.x);
        this.y = Math.max(this.y,other.y);
        this.z = Math.max(this.z,other.z);
        this.w = Math.max(this.w,other.w);
        return this;
    }
    public f32x4 cmax(f32x4 other) {
        return this.copy().max(other);
    }
    public f32x4 max(float x, float y, float z, float w) {
        this.x = Math.max(this.x,x);
        this.y = Math.max(this.y,y);
        this.z = Math.max(this.z,z);
        this.w = Math.max(this.w,w);
        return this;
    }
    public f32x4 cmax(float x, float y, float z, float w) {
        return this.copy().max(x, y, z, w);
    }
    public f32x4 min(f32x4 other) {
        this.x = Math.min(this.x,other.x);
        this.y = Math.min(this.y,other.y);
        this.z = Math.min(this.z,other.z);
        this.w = Math.min(this.w,other.w);
        return this;
    }
    public f32x4 cmin(f32x4 other) {
        return this.copy().min(other);
    }
    public f32x4 min(float x, float y, float z, float w) {
        this.x = Math.min(this.x,x);
        this.y = Math.min(this.y,y);
        this.z = Math.min(this.z,z);
        this.w = Math.min(this.w,w);
        return this;
    }
    public f32x4 cmin(float x, float y, float z, float w) {
        return this.copy().min(x, y, z, w);
    }
    public f32x4 sadd(float other) {
        this.x += other;
        this.y += other;
        this.z += other;
        this.w += other;
        return this;
    }
    public f32x4 csadd(float other) {
        return this.copy().sadd(other);
    }
    public f32x4 ssub(float other) {
        this.x -= other;
        this.y -= other;
        this.z -= other;
        this.w -= other;
        return this;
    }
    public f32x4 cssub(float other) {
        return this.copy().ssub(other);
    }
    public f32x4 smul(float other) {
        this.x *= other;
        this.y *= other;
        this.z *= other;
        this.w *= other;
        return this;
    }
    public f32x4 csmul(float other) {
        return this.copy().smul(other);
    }
    public f32x4 sdiv(float other) {
        this.x /= other;
        this.y /= other;
        this.z /= other;
        this.w /= other;
        return this;
    }
    public f32x4 csdiv(float other) {
        return this.copy().sdiv(other);
    }
    public f32x4 reset() {
        x = 0;
        y = 0;
        z = 0;
        w = 0;
        return this;
    }
    public f32x4 reset(float value) {
        x = value;
        y = value;
        z = value;
        w = value;
        return this;
    }
    public float dot(f32x4 other) {
        return x*other.x + y*other.y + z*other.z + w*other.w;
    }
    public float dist(f32x4 other) {
        return this.csub(other).mag();
    }
    public float distSq(f32x4 other) {
        return this.csub(other).magSq();
    }
    public float dot(float x, float y, float z, float w) {
        return x*x + y*y + z*z + w*w;
    }
    public float dist(float x, float y, float z, float w) {
        return this.csub(x, y, z, w).mag();
    }
    public float distSq(float x, float y, float z, float w) {
        return this.csub(x, y, z, w).magSq();
    }
    public f32x4 abs() {
        x = Math.abs(x);
        y = Math.abs(y);
        z = Math.abs(z);
        w = Math.abs(w);
        return this;
    }
    public f32x4 cabs() {
        return this.copy().abs();
    }
    public f32x4 inv() {
        x = 1/x;
        y = 1/y;
        z = 1/z;
        w = 1/w;
        return this;
    }
    public f32x4 cinv() {
        return this.copy().inv();
    }
    public float sum() {
        return x + y + z + w;
    }
    public float prod() {
        return x * y * z * w;
    }
    public float magSq() {
        return this.dot(this);
    }
    public float mag() {
        return (float)Math.sqrt(this.dot(this));
    }
    public float max() {
        return Math.max(Math.max(Math.max(x,y),z),w);
    }
    public float min() {
        return Math.min(Math.min(Math.min(x,y),z),w);
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
        glUniform4f(index, x, y, z, w);
    }
    public u32x4 u() {
        return new u32x4((int)x, (int)y, (int)z, (int)w);
    }
    public i32x4 i() {
        return new i32x4((int)x, (int)y, (int)z, (int)w);
    }
}