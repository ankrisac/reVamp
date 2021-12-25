package src.SGFX.Vec;
import src.SGFX.*;
import static org.lwjgl.opengl.GL43C.*;
// Generated class: Refer template/Main.java
public class f32x3 {
    public static class Arr {
        public float[] data;
        public Arr(int N) {
            data = new float[3 * N];
        }
        public int size() {
            return data.length / 3;
        }
        public void resize(int N) {
            float[] new_data = new float[3 * N];
            System.arraycopy(data, 0, new_data, 0, Math.min(data.length, new_data.length));
            data = new_data;
        }
        public void set(float[] other, int index, int offset) {
            System.arraycopy(other, 3 * index + offset, data, 0, other.length);
        }
        public void set(Arr other, int index, int offset) {
            System.arraycopy(other.data, 3 * index + offset, data, 0, other.size());
        }
        public void set(int index, f32x3 vec) {
            data[3 * index + 0] = vec.x;
            data[3 * index + 1] = vec.y;
            data[3 * index + 2] = vec.z;
        }
        public void set(int index, float x, float y, float z) {
            data[3 * index + 0] = x;
            data[3 * index + 1] = y;
            data[3 * index + 2] = z;
        }
        public f32x3 get(int index) {
            return new f32x3(data[3 * index + 0], data[3 * index + 1], data[3 * index + 2]);
        }
    }
    public static class Buf extends GL_Buf {
        public final Arr local;
        public Buf(int target, BufFmt.Usage usage, int reserve) {
            super(target, BufFmt.Block(BufFmt.Type.F32, 3, usage));
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
        public void update_range_unaligned(float[] data, int index, int offset) {
            bind();
            local.set(data, index, offset);
            glBufferSubData(target, 3*index + offset, data);
            unbind();
        }
        public void update_range_unaligned(Arr data, int index, int offset) {
            update_range_unaligned(data.data, index, offset);
        }
        public void update_range_unaligned(f32x3 data, int index, int offset) {
            update_range_unaligned(data.as_array(), index, offset);
        }
        public void update_range(Arr data, int index) {
            update_range_unaligned(data.data, index, 0);
        }
        public void update_range(f32x3 data, int index) {
            update_range_unaligned(data.as_array(), index, 0);
        }
    }
    public float x;
    public float y;
    public float z;
    public f32x3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public static f32x3 of(float x, float y, float z) {
        return new f32x3(x, y, z);
    }
    public f32x3 copy() {
        return new f32x3(x, y, z);
    }
    public float[] as_array() {
        float[] data = { x, y, z };
        return data;
    }
    public f32x3 add(f32x3 other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        return this;
    }
    public f32x3 cadd(f32x3 other) {
        return this.copy().add(other);
    }
    public f32x3 add(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }
    public f32x3 cadd(float x, float y, float z) {
        return this.copy().add(x, y, z);
    }
    public f32x3 sub(f32x3 other) {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        return this;
    }
    public f32x3 csub(f32x3 other) {
        return this.copy().sub(other);
    }
    public f32x3 sub(float x, float y, float z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }
    public f32x3 csub(float x, float y, float z) {
        return this.copy().sub(x, y, z);
    }
    public f32x3 mul(f32x3 other) {
        this.x *= other.x;
        this.y *= other.y;
        this.z *= other.z;
        return this;
    }
    public f32x3 cmul(f32x3 other) {
        return this.copy().mul(other);
    }
    public f32x3 mul(float x, float y, float z) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        return this;
    }
    public f32x3 cmul(float x, float y, float z) {
        return this.copy().mul(x, y, z);
    }
    public f32x3 div(f32x3 other) {
        this.x /= other.x;
        this.y /= other.y;
        this.z /= other.z;
        return this;
    }
    public f32x3 cdiv(f32x3 other) {
        return this.copy().div(other);
    }
    public f32x3 div(float x, float y, float z) {
        this.x /= x;
        this.y /= y;
        this.z /= z;
        return this;
    }
    public f32x3 cdiv(float x, float y, float z) {
        return this.copy().div(x, y, z);
    }
    public f32x3 reset() {
        x = 0;
        y = 0;
        z = 0;
        return this;
    }
    public f32x3 creset() {
        return this.copy().reset();
    }
    public f32x3 reset(float value) {
        x = value;
        y = value;
        z = value;
        return this;
    }
    public f32x3 creset(float value) {
        return this.copy().reset(value);
    }
    public float dot(f32x3 other) {
        return x*other.x + y*other.y + z*other.z;
    }
    public float dist(f32x3 other) {
        return this.csub(other).mag();
    }
    public float distSq(f32x3 other) {
        return this.csub(other).magSq();
    }
    public float dot(float x, float y, float z) {
        return x*x + y*y + z*z;
    }
    public float dist(float x, float y, float z) {
        return this.csub(x, y, z).mag();
    }
    public float distSq(float x, float y, float z) {
        return this.csub(x, y, z).magSq();
    }
    public f32x3 abs() {
        x = Math.abs(x);
        y = Math.abs(y);
        z = Math.abs(z);
        return this;
    }
    public f32x3 cabs() {
        return this.copy().abs();
    }
    public f32x3 inv() {
        x = 1/x;
        y = 1/y;
        z = 1/z;
        return this;
    }
    public f32x3 cinv() {
        return this.copy().inv();
    }
    public float sum() {
        return x + y + z;
    }
    public float prod() {
        return x * y * z;
    }
    public float magSq() {
        return this.dot(this);
    }
    public float mag() {
        return (float)Math.sqrt(this.dot(this));
    }
    public float max() {
        return Math.max(Math.max(x,y),z);
    }
    public float min() {
        return Math.min(Math.min(x,y),z);
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
        glUniform3f(index, x, y, z);
    }
    public u32x3 u() {
        return new u32x3((int)x, (int)y, (int)z);
    }
    public i32x3 i() {
        return new i32x3((int)x, (int)y, (int)z);
    }
}