package src.SGFX.Vec;
import src.SGFX.*;
import static org.lwjgl.opengl.GL43C.*;
// Generated class: Refer template/Main.java
public class u32x4 {
    public static class Arr {
        public int[] data;
        public Arr(int N) {
            data = new int[4 * N];
        }
        public int size() {
            return data.length / 4;
        }
        public void resize(int N) {
            int[] new_data = new int[4 * N];
            System.arraycopy(data, 0, new_data, 0, Math.min(data.length, new_data.length));
            data = new_data;
        }
        public void set(int[] other, int index, int offset) {
            System.arraycopy(other, 4 * index + offset, data, 0, other.length);
        }
        public void set(Arr other, int index, int offset) {
            System.arraycopy(other.data, 4 * index + offset, data, 0, other.size());
        }
        public void set(int index, u32x4 vec) {
            data[4 * index + 0] = vec.x;
            data[4 * index + 1] = vec.y;
            data[4 * index + 2] = vec.z;
            data[4 * index + 3] = vec.w;
        }
        public void set(int index, int x, int y, int z, int w) {
            data[4 * index + 0] = x;
            data[4 * index + 1] = y;
            data[4 * index + 2] = z;
            data[4 * index + 3] = w;
        }
        public u32x4 get(int index) {
            return new u32x4(data[4 * index + 0], data[4 * index + 1], data[4 * index + 2], data[4 * index + 3]);
        }
    }
    public static class Buf extends GL_Buf {
        public final Arr local;
        public Buf(int target, BufFmt.Usage usage, int reserve) {
            super(target, BufFmt.Block(BufFmt.Type.U32, 4, usage));
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
            glBufferSubData(target, 4*index + offset, data);
            unbind();
        }
        public void update_range_unaligned(Arr data, int index, int offset) {
            update_range_unaligned(data.data, index, offset);
        }
        public void update_range_unaligned(u32x4 data, int index, int offset) {
            update_range_unaligned(data.as_array(), index, offset);
        }
        public void update_range(Arr data, int index) {
            update_range_unaligned(data.data, index, 0);
        }
        public void update_range(u32x4 data, int index) {
            update_range_unaligned(data.as_array(), index, 0);
        }
    }
    public int x;
    public int y;
    public int z;
    public int w;
    public u32x4(int x, int y, int z, int w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    public static u32x4 of(int x, int y, int z, int w) {
        return new u32x4(x, y, z, w);
    }
    public u32x4 copy() {
        return new u32x4(x, y, z, w);
    }
    public int[] as_array() {
        int[] data = { x, y, z, w };
        return data;
    }
    public u32x4 add(u32x4 other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        this.w += other.w;
        return this;
    }
    public u32x4 cadd(u32x4 other) {
        return this.copy().add(other);
    }
    public u32x4 add(int x, int y, int z, int w) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.w += w;
        return this;
    }
    public u32x4 cadd(int x, int y, int z, int w) {
        return this.copy().add(x, y, z, w);
    }
    public u32x4 sub(u32x4 other) {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        this.w -= other.w;
        return this;
    }
    public u32x4 csub(u32x4 other) {
        return this.copy().sub(other);
    }
    public u32x4 sub(int x, int y, int z, int w) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        this.w -= w;
        return this;
    }
    public u32x4 csub(int x, int y, int z, int w) {
        return this.copy().sub(x, y, z, w);
    }
    public u32x4 mul(u32x4 other) {
        this.x *= other.x;
        this.y *= other.y;
        this.z *= other.z;
        this.w *= other.w;
        return this;
    }
    public u32x4 cmul(u32x4 other) {
        return this.copy().mul(other);
    }
    public u32x4 mul(int x, int y, int z, int w) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        this.w *= w;
        return this;
    }
    public u32x4 cmul(int x, int y, int z, int w) {
        return this.copy().mul(x, y, z, w);
    }
    public u32x4 div(u32x4 other) {
        this.x /= other.x;
        this.y /= other.y;
        this.z /= other.z;
        this.w /= other.w;
        return this;
    }
    public u32x4 cdiv(u32x4 other) {
        return this.copy().div(other);
    }
    public u32x4 div(int x, int y, int z, int w) {
        this.x /= x;
        this.y /= y;
        this.z /= z;
        this.w /= w;
        return this;
    }
    public u32x4 cdiv(int x, int y, int z, int w) {
        return this.copy().div(x, y, z, w);
    }
    public u32x4 reset() {
        x = 0;
        y = 0;
        z = 0;
        w = 0;
        return this;
    }
    public u32x4 creset() {
        return this.copy().reset();
    }
    public u32x4 reset(int value) {
        x = value;
        y = value;
        z = value;
        w = value;
        return this;
    }
    public u32x4 creset(int value) {
        return this.copy().reset(value);
    }
    public int dot(u32x4 other) {
        return x*other.x + y*other.y + z*other.z + w*other.w;
    }
    public int dist(u32x4 other) {
        return this.csub(other).mag();
    }
    public int distSq(u32x4 other) {
        return this.csub(other).magSq();
    }
    public int dot(int x, int y, int z, int w) {
        return x*x + y*y + z*z + w*w;
    }
    public int dist(int x, int y, int z, int w) {
        return this.csub(x, y, z, w).mag();
    }
    public int distSq(int x, int y, int z, int w) {
        return this.csub(x, y, z, w).magSq();
    }
    public u32x4 abs() {
        x = Math.abs(x);
        y = Math.abs(y);
        z = Math.abs(z);
        w = Math.abs(w);
        return this;
    }
    public u32x4 cabs() {
        return this.copy().abs();
    }
    public u32x4 inv() {
        x = 1/x;
        y = 1/y;
        z = 1/z;
        w = 1/w;
        return this;
    }
    public u32x4 cinv() {
        return this.copy().inv();
    }
    public int sum() {
        return x + y + z + w;
    }
    public int prod() {
        return x * y * z * w;
    }
    public int magSq() {
        return this.dot(this);
    }
    public int mag() {
        return (int)Math.sqrt(this.dot(this));
    }
    public int max() {
        return Math.max(Math.max(Math.max(x,y),z),w);
    }
    public int min() {
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
    public static class Index extends Buf implements Buf.Index {
        public Index(BufFmt.Usage usage, int reserve) {
            super(GL_ELEMENT_ARRAY_BUFFER, usage, reserve);
        }
    }
    public void bind(int index) {
        glUniform4ui(index, x, y, z, w);
    }
    public i32x4 i() {
        return new i32x4((int)x, (int)y, (int)z, (int)w);
    }
    public f32x4 f() {
        return new f32x4((float)x, (float)y, (float)z, (float)w);
    }
}