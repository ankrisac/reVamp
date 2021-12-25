package src;

import static org.lwjgl.opengl.GL43C.*;

import java.io.IOException;
import java.util.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;

import src.SGFX.*;
import src.Vec.*;

class FontAtlas implements Resource, Bindable {
    public final Tex2D atlas;
    public final Sampler sampler;

    public final i32x2 N;
    public final i32x2 glyph;
    public final float ratio;

    public FontAtlas() {
        sampler = new Sampler(Tex.Dim.D2)
                .filter(new TexFilter(TexFilter.Fn.Nearest, TexFilter.Fn.Nearest, TexFilter.MipMap.None))
                .wrap(new TexWrap(TexWrap.Axis.ClampBorder, TexWrap.Axis.ClampBorder));

        atlas = new Tex2D();
        atlas.load("assets/Termfont.png", false);
        sampler.bind(0);

        N = i32x2.of(16, 8);

        int[] size = atlas.getSize();

        glyph = i32x2.of(size[0], size[1]).div(N);
        ratio = glyph.f().grad();
    }

    public void destroy() {
        sampler.destroy();
        atlas.destroy();
    }

    public void bind() {
        atlas.bind();
    }

    public void unbind() {
        atlas.unbind();
    }
}

class TextMesh implements Resource {
    private u32x1.Index ibo;

    private f32x2.Attrib vert;
    private i32x2.Attrib cell_uv;
    private i32x2.Attrib cell_id;

    private BindGroup layout;

    public TextMesh() {
        ibo = new u32x1.Index(BufFmt.Usage.Draw);
        ibo.local = new u32x1.Arr(32);

        vert = new f32x2.Attrib(BufFmt.Usage.MutDraw);
        cell_uv = new i32x2.Attrib(BufFmt.Usage.MutDraw);
        cell_id = new i32x2.Attrib(BufFmt.Usage.MutDraw);

        vert.local = new f32x2.Arr(32);
        cell_uv.local = new i32x2.Arr(32);
        cell_id.local = new i32x2.Arr(32);

        layout = new BindGroup();
        layout.attach(0, vert);
        layout.attach(1, cell_uv);
        layout.attach(2, cell_id);
    }

    public void destroy() {
        layout.destroy();
        ibo.destroy();

        vert.destroy();
        cell_uv.destroy();
        cell_id.destroy();
    }

    public void draw() {
        layout.draw(BindGroup.DrawMode.Tris, ibo);
    }

    public void resize(i32x2 N, f32x2 ds) {
        int n = N.prod();

        i32x2[] cell_off = {
                i32x2.of(0, 1),
                i32x2.of(1, 1),
                i32x2.of(1, 0),
                i32x2.of(0, 0),
        };
        int cell_len = cell_off.length;
        int num_vertex = cell_off.length * n;

        vert.local.resize(num_vertex);
        cell_uv.local.resize(num_vertex);
        cell_id.local.resize(num_vertex);

        int[] cell_index = {
                0, 3, 1,
                1, 3, 2
        };

        ibo.local.resize(cell_index.length * n);

        int i_attrib = 0;
        int i_index = 0;

        for (int j = 0; j < N.y; j++) {
            for (int i = 0; i < N.x; i++) {
                for (i32x2 off : cell_off) {
                    vert.local.set(i_attrib, off.f().cadd(i, -j - 1).mul(ds).add(-1, 1));
                    cell_uv.local.set(i_attrib, off.x, 1 - off.y);
                    cell_id.local.set(i_attrib, i, j);
                    i_attrib++;
                }

                int base = cell_len * (i + j * N.x);
                for (int off : cell_index) {
                    ibo.local.set(i_index++, base + off);
                }
            }
        }

        vert.update();
        cell_uv.update();
        cell_id.update();
        ibo.update();

        layout.attach(0, vert);
        layout.attach(1, cell_uv);
        layout.attach(2, cell_id);
    }
}

class TextBuffer {
    public StringBuilder current;
    public ArrayList<StringBuilder> prev;
    public ArrayList<StringBuilder> next;

    public int cursor;

    public TextBuffer() {
        current = new StringBuilder();
        prev = new ArrayList<>();
        next = new ArrayList<>();
        cursor = 0;
    }

    public void moveNext() {
        if (next.size() > 0) {
            prev.add(current);
            current = next.remove(0);
            cursor = 0;
        }
    }

    public void movePrev() {
        if (prev.size() > 0) {
            next.add(0, current);
            current = prev.remove(prev.size() - 1);
            cursor = 0;
        }
    }

    public void newLine() {
        prev.add(current);
        current = new StringBuilder();
        cursor = 0;
    }

    public void deleteLine() {
        if (prev.size() > 0) {
            prev.remove(prev.size() - 1);
            cursor = 0;
        }
    }

    public char getChr() {
        return current.charAt(cursor);
    }

    public void write(char chr) {
        current.append(chr);
        cursor++;
    }

    public Boolean delete() {
        if (current.length() > 0) {
            current.deleteCharAt(current.length() - 1);
            cursor--;
            return true;
        } else {
            return false;
        }
    }
}

class Terminal implements Resource {
    private Pipeline pipeline;
    private FontAtlas atlas;
    private TextMesh mesh;
    private i32x2.Storage glyph_buffer;
    private TextBuffer text;

    private int Nx;
    private int Ny;

    private float dx;
    private float dy;

    private i32x2 cell = i32x2.of(0, 0);

    private int origin_y;

    public Terminal() throws IOException {
        glyph_buffer = new i32x2.Storage(BufFmt.Usage.MutDraw);
        glyph_buffer.local = new i32x2.Arr(1024);

        text = new TextBuffer();

        text.prev.add(new StringBuilder("1. hello world"));
        text.prev.add(new StringBuilder("2. hello world"));
        text.prev.add(new StringBuilder("3. hello world"));
        text.prev.add(new StringBuilder("4. hello world"));

        text.current = new StringBuilder("hello world");

        text.next.add(new StringBuilder("5. hello world"));
        text.next.add(new StringBuilder("6. hello world"));
        text.next.add(new StringBuilder("7. hello world"));

        origin_y = 0;

        mesh = new TextMesh();
        atlas = new FontAtlas();
        pipeline = new Pipeline("text-panel", "shaders/text");
    }

    public void destroy() {
        pipeline.destroy();
        atlas.destroy();
        mesh.destroy();
        glyph_buffer.destroy();
    }

    float t = 0;

    public void draw() {
        pipeline.bind();

        atlas.N.f().inv().bind(0);
        f32x2.of(0.0f, 0.0f).bind(1);
        i32x2.of(Nx, Ny).bind(2);
        i32x2.of(0, origin_y).bind(3);

        glyph_buffer.set_binding(0);

        atlas.bind();
        mesh.draw();
        atlas.unbind();
        pipeline.unbind();

        t = (t + 0.02f) % 1.0f;
    }

    public void meshWriteLine(i32x2.Arr arr_glyph, int j, String line) {
        for (int i = 0; i < Math.min(line.length(), Nx); i++) {
            char chr = line.charAt(i);
            int index = (i + j * Nx);
            arr_glyph.set(index, i32x2.of(chr % atlas.N.x, chr / atlas.N.x));
        }
    }

    public int meshWrite(i32x2.Arr arr_glyph, int j, List<String> lines) {
        for (String line : lines) {
            if (j >= Ny)
                break;
            meshWriteLine(arr_glyph, j++, line);
        }
        return j;
    }

    public void refresh_mesh() {
        int N = Nx * Ny;
        glyph_buffer.local.resize(N);
        {
            char space = ' ';
            i32x2 off = i32x2.of(space % atlas.N.x, space / atlas.N.x);

            for (int i = 0; i < N; i++) {
                glyph_buffer.local.set(i, off);
            }
        }

        int j = 0;
        int line_limit = Ny;

        {
            List<String> lines = new ArrayList<>();
            outer: for (StringBuilder line : text.prev) {
                for (String wline : line.toString().split("\n")) {
                    if (line_limit > 0)
                        break outer;
                    lines.add(wline);
                }
            }

            j = meshWrite(glyph_buffer.local, j, lines);
        }

        meshWriteLine(glyph_buffer.local, j, ">>" + text.current.toString());

        {
            List<String> lines = new ArrayList<>();
            outer: for (StringBuilder line : text.next) {
                for (String wline : line.toString().split("\n")) {
                    if (line_limit > 0)
                        break outer;
                    lines.add(wline);
                }
            }

            meshWrite(glyph_buffer.local, j + 1, lines);
        }
        glyph_buffer.update();
    }

    public void resize(int w, int h) {
        int font_size = 2;

        Nx = w / (font_size * atlas.glyph.x);
        dx = 2.0f / Nx;
        dy = dx * atlas.ratio * (float) w / (float) h;
        Ny = (int) (2.0f / dy) + 1;

        cell.x = 0;
        cell.y = 0;

        refresh_mesh();
        mesh.resize(i32x2.of(Nx, Ny), f32x2.of(dx, dy));
    }

    public void writeAt(char chr, i32x2 c) {
        int val = (int) chr;

        i32x2.Arr data = new i32x2.Arr(1);
        data.set(0, val % atlas.N.x, val / atlas.N.x);

        int index = 8 * (c.x + Nx * c.y); // I32;

        glyph_buffer.update(data, index, 0);
    }

    public void write(char chr) {
        switch (chr) {
            case '\r':
                cell.x = 0;
                break;
            case '\n':
                next();
                break;
            default: {
                writeAt(chr, cell);
                break;
            }
        }

        cell.x++;
        if (cell.x >= Nx) {
            next();
        }
    }

    public void pop() {
        writeAt(' ', cell); // Remove cursor also
        if (cell.x > 0) {
            cell.x--;
        } else if (cell.y > 0) {
            cell.y--;
        }
        writeAt(' ', cell);

    }

    public void next() {
        writeAt(' ', cell); // Remove cursor also
        cell.y++;
        cell.x = 0;
    }

    public void up() {
        text.movePrev();
        refresh_mesh();
    }

    public void down() {
        text.moveNext();
        refresh_mesh();
    }

    public void left() {
        writeAt(' ', cell);
        if (cell.x > 0) {
            cell.x--;
        }
    }

    public void right() {
        writeAt(' ', cell);
        if (cell.x < Nx - 1) {
            cell.x++;
        }
    }
}

public class Main {
    static public Boolean fill = true;

    static public void toggle_fill() {
        if (fill) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        } else {
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }
        fill = !fill;
    }

    public static void main(String args[]) throws Throwable {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            throw new RuntimeException("GLFW init failed");
        }

        try {
            Window win = new Window(500, 500, "Terminal");
            win.context_focus();
            GL.createCapabilities();

            glEnable(GL_CULL_FACE);
            glCullFace(GL_BACK);
            glFrontFace(GL_CCW);

            Terminal term = new Terminal();
            term.resize(800, 800);

            GLFW.glfwSetWindowSizeCallback(win.handle, (window, w, h) -> {
                int[] dim = win.getSize();
                glViewport(0, 0, dim[0], dim[1]);
                term.resize(w, h);
            });
            GLFW.glfwSetCharCallback(win.handle, (window, codepoint) -> {
                if (codepoint < 128) {
                    term.write((char) codepoint);
                } else {
                    term.write('\\');
                    term.write('?');
                }
            });

            GLFW.glfwSetKeyCallback(win.handle, (window, key, scancode, action, mod) -> {
                if (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT) {
                    switch (key) {
                        case GLFW.GLFW_KEY_BACKSPACE:
                        case GLFW.GLFW_KEY_DELETE:
                            term.pop();
                            break;
                        case GLFW.GLFW_KEY_ENTER:
                            term.next();
                            break;
                        case GLFW.GLFW_KEY_UP:
                            term.up();
                            break;
                        case GLFW.GLFW_KEY_DOWN:
                            term.down();
                            break;
                        case GLFW.GLFW_KEY_LEFT:
                            term.left();
                            break;
                        case GLFW.GLFW_KEY_RIGHT:
                            term.right();
                            break;
                        case GLFW.GLFW_KEY_ESCAPE: {
                            toggle_fill();
                            break;
                        }
                        default:
                            break;
                    }
                }
            });

            while (win.is_open()) {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glClearColor(0.0f, 0.0f, 0.14f, 1.0f);
                term.draw();

                win.swap();
                GLFW.glfwPollEvents();
            }

            term.destroy();
            win.destroy();
        } catch (Exception ex) {
            System.out.println("[Caught Exception]:\n" + ex.getMessage());
            throw ex;
        }

        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }
}