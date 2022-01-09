package src.SGFX.Vec;
import src.SGFX.*;
import static org.lwjgl.opengl.GL43C.*;
// Generated class: Refer template/Main.java
public class u32x2 {
    public static class Arr {
        public int[] data;
        public Arr(int N) {
            data = new int[2 * N];
        }
        public int size() {
            return data.length / 2;
        }
        public static Arr of(int... data) {
            if (data.length % 2 != 0) {
                throw new RuntimeException("Data length must be multiple of vector dimension");
            }
            Arr out = new Arr(data.length);
            out.set_unaligned(data, 0, 0);
            return out;
        }
        public void resize(int N) {
            int[] new_data = new int[2 * N];
            System.arraycopy(data, 0, new_data, 0, Math.min(data.length, new_data.length));
            data = new_data;
        }
        public void set_unaligned(int[] other, int index, int offset) {
            System.arraycopy(other, 2 * index + offset, data, 0, other.length);
        }
        public void set_unaligned(Arr other, int index, int offset) {
            System.arraycopy(other.data, 2 * index + offset, data, 0, other.size());
        }
        public void set(int index, u32x2 vec) {
            data[2 * index + 0] = vec.x;
            data[2 * index + 1] = vec.y;
        }
        public void set(int index, int x, int y) {
            data[2 * index + 0] = x;
            data[2 * index + 1] = y;
        }
        public u32x2 get(int index) {
            return new u32x2(data[2 * index + 0], data[2 * index + 1]);
        }
    }
    public static class Buf extends GL_Buf {
        public Arr local;
        public Buf(int target, BufFmt.Usage usage, int reserve) {
            super(target, BufFmt.Block(BufFmt.Type.U32, 2, usage));
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
            glBufferSubData(target, 2*index + offset, data);
            unbind();
        }
        public void write_unaligned(Arr data, int index, int offset) {
            write_unaligned(data.data, index, offset);
        }
        public void write_unaligned(u32x2 data, int index, int offset) {
            write_unaligned(data.as_array(), index, offset);
        }
        public void write(Arr data, int index) {
            write_unaligned(data.data, index, 0);
        }
        public void write(u32x2 data, int index) {
            write_unaligned(data.as_array(), index, 0);
        }
    }
    public int x;
    public int y;
    public u32x2(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public u32x2 copy() {
        return new u32x2(x, y);
    }
    public static u32x2 of(int x, int y) {
        return new u32x2(x, y);
    }
    public String toString() {
        return "[" + x + "," + y + "]";
    }
    public static u32x2 zero() {
        return of(0, 0);
    }
    public static u32x2 val(int val) {
        return of(val, val);
    }
    public u32x2 set(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }
    public u32x2 set(u32x2 other) {
        this.x = other.x;
        this.y = other.y;
        return this;
    }
    public int[] as_array() {
        int[] data = { x, y };
        return data;
    }
    public u32x2 add(u32x2 other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }
    public u32x2 cadd(u32x2 other) {
        return this.copy().add(other);
    }
    public u32x2 add(int x, int y) {
        this.x += x;
        this.y += y;
        return this;
    }
    public u32x2 cadd(int x, int y) {
        return this.copy().add(x, y);
    }
    public u32x2 sub(u32x2 other) {
        this.x -= other.x;
        this.y -= other.y;
        return this;
    }
    public u32x2 csub(u32x2 other) {
        return this.copy().sub(other);
    }
    public u32x2 sub(int x, int y) {
        this.x -= x;
        this.y -= y;
        return this;
    }
    public u32x2 csub(int x, int y) {
        return this.copy().sub(x, y);
    }
    public u32x2 mul(u32x2 other) {
        this.x *= other.x;
        this.y *= other.y;
        return this;
    }
    public u32x2 cmul(u32x2 other) {
        return this.copy().mul(other);
    }
    public u32x2 mul(int x, int y) {
        this.x *= x;
        this.y *= y;
        return this;
    }
    public u32x2 cmul(int x, int y) {
        return this.copy().mul(x, y);
    }
    public u32x2 div(u32x2 other) {
        this.x /= other.x;
        this.y /= other.y;
        return this;
    }
    public u32x2 cdiv(u32x2 other) {
        return this.copy().div(other);
    }
    public u32x2 div(int x, int y) {
        this.x /= x;
        this.y /= y;
        return this;
    }
    public u32x2 cdiv(int x, int y) {
        return this.copy().div(x, y);
    }
    public u32x2 max(u32x2 other) {
        this.x = Math.max(this.x,other.x);
        this.y = Math.max(this.y,other.y);
        return this;
    }
    public u32x2 cmax(u32x2 other) {
        return this.copy().max(other);
    }
    public u32x2 max(int x, int y) {
        this.x = Math.max(this.x,x);
        this.y = Math.max(this.y,y);
        return this;
    }
    public u32x2 cmax(int x, int y) {
        return this.copy().max(x, y);
    }
    public u32x2 min(u32x2 other) {
        this.x = Math.min(this.x,other.x);
        this.y = Math.min(this.y,other.y);
        return this;
    }
    public u32x2 cmin(u32x2 other) {
        return this.copy().min(other);
    }
    public u32x2 min(int x, int y) {
        this.x = Math.min(this.x,x);
        this.y = Math.min(this.y,y);
        return this;
    }
    public u32x2 cmin(int x, int y) {
        return this.copy().min(x, y);
    }
    public u32x2 sadd(int other) {
        this.x += other;
        this.y += other;
        return this;
    }
    public u32x2 csadd(int other) {
        return this.copy().sadd(other);
    }
    public u32x2 ssub(int other) {
        this.x -= other;
        this.y -= other;
        return this;
    }
    public u32x2 cssub(int other) {
        return this.copy().ssub(other);
    }
    public u32x2 smul(int other) {
        this.x *= other;
        this.y *= other;
        return this;
    }
    public u32x2 csmul(int other) {
        return this.copy().smul(other);
    }
    public u32x2 sdiv(int other) {
        this.x /= other;
        this.y /= other;
        return this;
    }
    public u32x2 csdiv(int other) {
        return this.copy().sdiv(other);
    }
    public u32x2 reset() {
        x = 0;
        y = 0;
        return this;
    }
    public u32x2 reset(int value) {
        x = value;
        y = value;
        return this;
    }
    public int dot(u32x2 other) {
        return x*other.x + y*other.y;
    }
    public int dist(u32x2 other) {
        return this.csub(other).mag();
    }
    public int distSq(u32x2 other) {
        return this.csub(other).magSq();
    }
    public int dot(int x, int y) {
        return x*x + y*y;
    }
    public int dist(int x, int y) {
        return this.csub(x, y).mag();
    }
    public int distSq(int x, int y) {
        return this.csub(x, y).magSq();
    }
    public u32x2 abs() {
        x = Math.abs(x);
        y = Math.abs(y);
        return this;
    }
    public u32x2 cabs() {
        return this.copy().abs();
    }
    public u32x2 inv() {
        x = 1/x;
        y = 1/y;
        return this;
    }
    public u32x2 cinv() {
        return this.copy().inv();
    }
    public int sum() {
        return x + y;
    }
    public int prod() {
        return x * y;
    }
    public int magSq() {
        return this.dot(this);
    }
    public int mag() {
        return (int)Math.sqrt(this.dot(this));
    }
    public int max() {
        return Math.max(x,y);
    }
    public int min() {
        return Math.min(x,y);
    }
    public int grad() {
        return y / x;
    }
    public int invgrad() {
        return x / y;
    }
    public u32x2 Jconj() {
        y = -y;
        return this;
    }
    public u32x2 cJconj() {
        return this.copy().Jconj();
    }
    public u32x2 Jinv() {
        int mag = x * x + y * y;
        x /= mag;
        y /= mag;
        return this;
    }
    public u32x2 cJinv() {
        return this.copy().Jinv();
    }
    public u32x2 Jmul(u32x2 other) {
        int a = x * other.x + y * other.y;
        int b = x * other.y - y * other.x;
        x = a;
        y = b;
        return this;
    }
    public u32x2 cJmul(u32x2 other) {
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
        glUniform2ui(index, x, y);
    }
    public i32x2 i() {
        return new i32x2((int)x, (int)y);
    }
    public f32x2 f() {
        return new f32x2((float)x, (float)y);
    }
}