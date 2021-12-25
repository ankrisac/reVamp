package src.SGFX.Vec;
import src.SGFX.*;
import static org.lwjgl.opengl.GL43C.*;
// Generated class: Refer template/Main.java
public class i32x2 {
    public static class Arr {
        public int[] data;
        public Arr(int N) {
            data = new int[2 * N];
        }
        public int size() {
            return data.length / 2;
        }
        public void resize(int N) {
            int[] new_data = new int[2 * N];
            System.arraycopy(data, 0, new_data, 0, Math.min(data.length, new_data.length));
            data = new_data;
        }
        public void set(int[] other, int index, int offset) {
            System.arraycopy(other, 2 * index + offset, data, 0, other.length);
        }
        public void set(Arr other, int index, int offset) {
            System.arraycopy(other.data, 2 * index + offset, data, 0, other.size());
        }
        public void set(int index, i32x2 vec) {
            data[2 * index + 0] = vec.x;
            data[2 * index + 1] = vec.y;
        }
        public void set(int index, int x, int y) {
            data[2 * index + 0] = x;
            data[2 * index + 1] = y;
        }
        public i32x2 get(int index) {
            return new i32x2(data[2 * index + 0], data[2 * index + 1]);
        }
    }
    public static class Buf extends GL_Buf {
        public final Arr local;
        public Buf(int target, BufFmt.Usage usage, int reserve) {
            super(target, BufFmt.Block(BufFmt.Type.I32, 2, usage));
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
            glBufferSubData(target, 2*index + offset, data);
            unbind();
        }
        public void update_range_unaligned(Arr data, int index, int offset) {
            update_range_unaligned(data.data, index, offset);
        }
        public void update_range_unaligned(i32x2 data, int index, int offset) {
            update_range_unaligned(data.as_array(), index, offset);
        }
        public void update_range(Arr data, int index) {
            update_range_unaligned(data.data, index, 0);
        }
        public void update_range(i32x2 data, int index) {
            update_range_unaligned(data.as_array(), index, 0);
        }
    }
    public int x;
    public int y;
    public i32x2(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public static i32x2 of(int x, int y) {
        return new i32x2(x, y);
    }
    public i32x2 copy() {
        return new i32x2(x, y);
    }
    public int[] as_array() {
        int[] data = { x, y };
        return data;
    }
    public i32x2 add(i32x2 other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }
    public i32x2 cadd(i32x2 other) {
        return this.copy().add(other);
    }
    public i32x2 add(int x, int y) {
        this.x += x;
        this.y += y;
        return this;
    }
    public i32x2 cadd(int x, int y) {
        return this.copy().add(x, y);
    }
    public i32x2 sub(i32x2 other) {
        this.x -= other.x;
        this.y -= other.y;
        return this;
    }
    public i32x2 csub(i32x2 other) {
        return this.copy().sub(other);
    }
    public i32x2 sub(int x, int y) {
        this.x -= x;
        this.y -= y;
        return this;
    }
    public i32x2 csub(int x, int y) {
        return this.copy().sub(x, y);
    }
    public i32x2 mul(i32x2 other) {
        this.x *= other.x;
        this.y *= other.y;
        return this;
    }
    public i32x2 cmul(i32x2 other) {
        return this.copy().mul(other);
    }
    public i32x2 mul(int x, int y) {
        this.x *= x;
        this.y *= y;
        return this;
    }
    public i32x2 cmul(int x, int y) {
        return this.copy().mul(x, y);
    }
    public i32x2 div(i32x2 other) {
        this.x /= other.x;
        this.y /= other.y;
        return this;
    }
    public i32x2 cdiv(i32x2 other) {
        return this.copy().div(other);
    }
    public i32x2 div(int x, int y) {
        this.x /= x;
        this.y /= y;
        return this;
    }
    public i32x2 cdiv(int x, int y) {
        return this.copy().div(x, y);
    }
    public i32x2 reset() {
        x = 0;
        y = 0;
        return this;
    }
    public i32x2 creset() {
        return this.copy().reset();
    }
    public i32x2 reset(int value) {
        x = value;
        y = value;
        return this;
    }
    public i32x2 creset(int value) {
        return this.copy().reset(value);
    }
    public int dot(i32x2 other) {
        return x*other.x + y*other.y;
    }
    public int dist(i32x2 other) {
        return this.csub(other).mag();
    }
    public int distSq(i32x2 other) {
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
    public i32x2 abs() {
        x = Math.abs(x);
        y = Math.abs(y);
        return this;
    }
    public i32x2 cabs() {
        return this.copy().abs();
    }
    public i32x2 inv() {
        x = 1/x;
        y = 1/y;
        return this;
    }
    public i32x2 cinv() {
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
    public i32x2 Jconj() {
        y = -y;
        return this;
    }
    public i32x2 cJconj() {
        return this.copy().Jconj();
    }
    public i32x2 Jinv() {
        int mag = x * x + y * y;
        x /= mag;
        y /= mag;
        return this;
    }
    public i32x2 cJinv() {
        return this.copy().Jinv();
    }
    public i32x2 Jmul(i32x2 other) {
        int a = x * other.x + y * other.y;
        int b = x * other.y - y * other.x;
        x = a;
        y = b;
        return this;
    }
    public i32x2 cJmul(i32x2 other) {
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
        glUniform2i(index, x, y);
    }
    public u32x2 u() {
        return new u32x2((int)x, (int)y);
    }
    public f32x2 f() {
        return new f32x2((float)x, (float)y);
    }
}