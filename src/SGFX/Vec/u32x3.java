package src.SGFX.Vec;
import src.SGFX.*;
import static org.lwjgl.opengl.GL43C.*;
// Generated class: Refer template/Main.java
public class u32x3 {
    public static class Arr {
        public int[] data;
        public Arr(int N) {
            data = new int[3 * N];
        }
        public int size() {
            return data.length / 3;
        }
        public static Arr of(int... data) {
            if (data.length % 3 != 0) {
                throw new RuntimeException("Data length must be multiple of vector dimension");
            }
            Arr out = new Arr(data.length);
            out.set_unaligned(data, 0, 0);
            return out;
        }
        public void resize(int N) {
            int[] new_data = new int[3 * N];
            System.arraycopy(data, 0, new_data, 0, Math.min(data.length, new_data.length));
            data = new_data;
        }
        public void set_unaligned(int[] other, int index, int offset) {
            System.arraycopy(other, 3 * index + offset, data, 0, other.length);
        }
        public void set_unaligned(Arr other, int index, int offset) {
            System.arraycopy(other.data, 3 * index + offset, data, 0, other.size());
        }
        public void set(int index, u32x3 vec) {
            data[3 * index + 0] = vec.x;
            data[3 * index + 1] = vec.y;
            data[3 * index + 2] = vec.z;
        }
        public void set(int index, int x, int y, int z) {
            data[3 * index + 0] = x;
            data[3 * index + 1] = y;
            data[3 * index + 2] = z;
        }
        public u32x3 get(int index) {
            return new u32x3(data[3 * index + 0], data[3 * index + 1], data[3 * index + 2]);
        }
    }
    public static class Buf extends GL_Buf {
        public Arr local;
        public Buf(int target, BufFmt.Usage usage, int reserve) {
            super(target, BufFmt.Block(BufFmt.Type.U32, 3, usage));
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
            glBufferSubData(target, 3*index + offset, data);
            unbind();
        }
        public void write_unaligned(Arr data, int index, int offset) {
            write_unaligned(data.data, index, offset);
        }
        public void write_unaligned(u32x3 data, int index, int offset) {
            write_unaligned(data.as_array(), index, offset);
        }
        public void write(Arr data, int index) {
            write_unaligned(data.data, index, 0);
        }
        public void write(u32x3 data, int index) {
            write_unaligned(data.as_array(), index, 0);
        }
    }
    public int x;
    public int y;
    public int z;
    public u32x3(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public u32x3 copy() {
        return new u32x3(x, y, z);
    }
    public static u32x3 of(int x, int y, int z) {
        return new u32x3(x, y, z);
    }
    public String toString() {
        return "[" + x + "," + y + "," + z + "]";
    }
    public static u32x3 zero() {
        return of(0, 0, 0);
    }
    public static u32x3 val(int val) {
        return of(val, val, val);
    }
    public u32x3 set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }
    public u32x3 set(u32x3 other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        return this;
    }
    public int[] as_array() {
        int[] data = { x, y, z };
        return data;
    }
    public u32x3 add(u32x3 other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        return this;
    }
    public u32x3 cadd(u32x3 other) {
        return this.copy().add(other);
    }
    public u32x3 add(int x, int y, int z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }
    public u32x3 cadd(int x, int y, int z) {
        return this.copy().add(x, y, z);
    }
    public u32x3 sub(u32x3 other) {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        return this;
    }
    public u32x3 csub(u32x3 other) {
        return this.copy().sub(other);
    }
    public u32x3 sub(int x, int y, int z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }
    public u32x3 csub(int x, int y, int z) {
        return this.copy().sub(x, y, z);
    }
    public u32x3 mul(u32x3 other) {
        this.x *= other.x;
        this.y *= other.y;
        this.z *= other.z;
        return this;
    }
    public u32x3 cmul(u32x3 other) {
        return this.copy().mul(other);
    }
    public u32x3 mul(int x, int y, int z) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        return this;
    }
    public u32x3 cmul(int x, int y, int z) {
        return this.copy().mul(x, y, z);
    }
    public u32x3 div(u32x3 other) {
        this.x /= other.x;
        this.y /= other.y;
        this.z /= other.z;
        return this;
    }
    public u32x3 cdiv(u32x3 other) {
        return this.copy().div(other);
    }
    public u32x3 div(int x, int y, int z) {
        this.x /= x;
        this.y /= y;
        this.z /= z;
        return this;
    }
    public u32x3 cdiv(int x, int y, int z) {
        return this.copy().div(x, y, z);
    }
    public u32x3 max(u32x3 other) {
        this.x = Math.max(this.x,other.x);
        this.y = Math.max(this.y,other.y);
        this.z = Math.max(this.z,other.z);
        return this;
    }
    public u32x3 cmax(u32x3 other) {
        return this.copy().max(other);
    }
    public u32x3 max(int x, int y, int z) {
        this.x = Math.max(this.x,x);
        this.y = Math.max(this.y,y);
        this.z = Math.max(this.z,z);
        return this;
    }
    public u32x3 cmax(int x, int y, int z) {
        return this.copy().max(x, y, z);
    }
    public u32x3 min(u32x3 other) {
        this.x = Math.min(this.x,other.x);
        this.y = Math.min(this.y,other.y);
        this.z = Math.min(this.z,other.z);
        return this;
    }
    public u32x3 cmin(u32x3 other) {
        return this.copy().min(other);
    }
    public u32x3 min(int x, int y, int z) {
        this.x = Math.min(this.x,x);
        this.y = Math.min(this.y,y);
        this.z = Math.min(this.z,z);
        return this;
    }
    public u32x3 cmin(int x, int y, int z) {
        return this.copy().min(x, y, z);
    }
    public u32x3 sadd(int other) {
        this.x += other;
        this.y += other;
        this.z += other;
        return this;
    }
    public u32x3 csadd(int other) {
        return this.copy().sadd(other);
    }
    public u32x3 ssub(int other) {
        this.x -= other;
        this.y -= other;
        this.z -= other;
        return this;
    }
    public u32x3 cssub(int other) {
        return this.copy().ssub(other);
    }
    public u32x3 smul(int other) {
        this.x *= other;
        this.y *= other;
        this.z *= other;
        return this;
    }
    public u32x3 csmul(int other) {
        return this.copy().smul(other);
    }
    public u32x3 sdiv(int other) {
        this.x /= other;
        this.y /= other;
        this.z /= other;
        return this;
    }
    public u32x3 csdiv(int other) {
        return this.copy().sdiv(other);
    }
    public u32x3 reset() {
        x = 0;
        y = 0;
        z = 0;
        return this;
    }
    public u32x3 reset(int value) {
        x = value;
        y = value;
        z = value;
        return this;
    }
    public int dot(u32x3 other) {
        return x*other.x + y*other.y + z*other.z;
    }
    public int dist(u32x3 other) {
        return this.csub(other).mag();
    }
    public int distSq(u32x3 other) {
        return this.csub(other).magSq();
    }
    public int dot(int x, int y, int z) {
        return x*x + y*y + z*z;
    }
    public int dist(int x, int y, int z) {
        return this.csub(x, y, z).mag();
    }
    public int distSq(int x, int y, int z) {
        return this.csub(x, y, z).magSq();
    }
    public u32x3 abs() {
        x = Math.abs(x);
        y = Math.abs(y);
        z = Math.abs(z);
        return this;
    }
    public u32x3 cabs() {
        return this.copy().abs();
    }
    public u32x3 inv() {
        x = 1/x;
        y = 1/y;
        z = 1/z;
        return this;
    }
    public u32x3 cinv() {
        return this.copy().inv();
    }
    public int sum() {
        return x + y + z;
    }
    public int prod() {
        return x * y * z;
    }
    public int magSq() {
        return this.dot(this);
    }
    public int mag() {
        return (int)Math.sqrt(this.dot(this));
    }
    public int max() {
        return Math.max(Math.max(x,y),z);
    }
    public int min() {
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
        glUniform3ui(index, x, y, z);
    }
    public i32x3 i() {
        return new i32x3((int)x, (int)y, (int)z);
    }
    public f32x3 f() {
        return new f32x3((float)x, (float)y, (float)z);
    }
}