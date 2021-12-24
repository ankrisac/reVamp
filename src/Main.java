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

    public final int Nx;
    public final int Ny;

    public final int glyph_x;
    public final int glyph_y;

    public final float ratio;

    public FontAtlas() {
        sampler = new Sampler(Tex.Dim.D2)
                .filter(new TexFilter(TexFilter.Fn.Nearest, TexFilter.Fn.Nearest, TexFilter.MipMap.None))
                .wrap(new TexWrap(TexWrap.Axis.ClampBorder, TexWrap.Axis.ClampBorder));

        atlas = new Tex2D();
        atlas.load("assets/Termfont.png", false);
        sampler.bind(0);

        Nx = 16;
        Ny = 8;

        int[] size = atlas.getSize();

        glyph_x = size[0] / Nx;
        glyph_y = size[1] / Ny;
        ratio = (float) glyph_y / (float) glyph_x;
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
    private BufIndex ibo;

    private BufAttrib vert;
    private BufAttrib cell_uv;
    private BufAttrib cell_id;

    private BindGroup layout;

    public TextMesh() {
        ibo = new BufIndex(BufFmt.Block(BufFmt.Type.U32, 1, BufFmt.Usage.Draw));

        vert = new BufAttrib(BufFmt.Block(BufFmt.Type.F32, 2, BufFmt.Usage.MutDraw));
        cell_uv = new BufAttrib(BufFmt.Block(BufFmt.Type.I32, 2, BufFmt.Usage.MutDraw));
        cell_id = new BufAttrib(BufFmt.Block(BufFmt.Type.I32, 2, BufFmt.Usage.MutDraw));

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

    public void resize(ivec2 N, fvec2 ds) {
        int n = N.prod();

        ivec2[] cell_off = {
                ivec2.of(0, 1),
                ivec2.of(1, 1),
                ivec2.of(1, 0),
                ivec2.of(0, 0),
        };
        int cell_len = cell_off.length;
        int num_vertex = cell_off.length * n;

        fvec2.Arr data_vert = new fvec2.Arr(num_vertex);
        ivec2.Arr data_cell_uv = new ivec2.Arr(num_vertex);
        ivec2.Arr data_cell_id = new ivec2.Arr(num_vertex);

        int[] cell_index = {
                0, 3, 1,
                1, 3, 2
        };
        int[] data_ind = new int[cell_index.length * n];

        int i_attrib = 0;
        int i_index = 0;

        for (int j = 0; j < N.y; j++) {
            for (int i = 0; i < N.x; i++) {
                for (ivec2 off : cell_off) {
                    data_vert.set(i_attrib, off.f().cadd(i, -j - 1).mul(ds).add(-1, 1));
                    data_cell_uv.set(i_attrib, ivec2.of(off.x, 1 - off.y));
                    data_cell_id.set(i_attrib, ivec2.of(i, j));
                    i_attrib++;
                }

                int base = cell_len * (i + j * N.x);
                for (int off : cell_index) {
                    data_ind[i_index++] = base + off;
                }
            }
        }

        vert.load(data_vert.data);
        cell_uv.load(data_cell_uv.data);
        cell_id.load(data_cell_id.data);
        ibo.load(data_ind);

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
    private BufStorage glyph_buffer;
    private TextBuffer text;

    private int Nx;
    private int Ny;

    private float dx;
    private float dy;

    private ivec2 cell = ivec2.of(0, 0);

    private int origin_y;

    public Terminal() throws IOException {
        glyph_buffer = new BufStorage(BufFmt.Block(BufFmt.Type.I32, 2, BufFmt.Usage.MutDraw));
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

        fvec2.of(1.0f / atlas.Nx, 1.0f / atlas.Ny).gl_send(0);
        glUniform1f(1, 0.0f);
        ivec2.of(Nx, Ny).gl_send(2);
        ivec2.of(0, origin_y).gl_send(3);

        glyph_buffer.set_binding(0);
        atlas.bind();
        mesh.draw();
        atlas.unbind();
        pipeline.unbind();

        t = (t + 0.02f) % 1.0f;
    }

    public void meshWriteLine(ivec2.Arr arr_glyph, int j, String line) {
        for (int i = 0; i < Math.min(line.length(), Nx); i++) {
            char chr = line.charAt(i);
            int index = (i + j * Nx);
            arr_glyph.set(index, ivec2.of(chr % atlas.Nx, chr / atlas.Nx));
        }
    }

    public int meshWrite(ivec2.Arr arr_glyph, int j, List<String> lines) {
        for (String line : lines) {
            if (j >= Ny)
                break;
            meshWriteLine(arr_glyph, j++, line);
        }
        return j;
    }

    public void refresh_mesh() {
        int N = Nx * Ny;
        ivec2.Arr arr_glyph = new ivec2.Arr(N);
        {
            char space = ' ';
            ivec2 off = ivec2.of(space % atlas.Nx, space / atlas.Nx);

            for (int i = 0; i < N; i++) {
                arr_glyph.set(i, off);
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

            j = meshWrite(arr_glyph, j, lines);
        }

        meshWriteLine(arr_glyph, j, ">>" + text.current.toString());

        {
            List<String> lines = new ArrayList<>();
            outer: for (StringBuilder line : text.next) {
                for (String wline : line.toString().split("\n")) {
                    if (line_limit > 0)
                        break outer;
                    lines.add(wline);
                }
            }

            meshWrite(arr_glyph, j + 1, lines);
        }

        glyph_buffer.load(arr_glyph.data);
    }

    public void resize(int w, int h) {
        int font_size = 2;

        Nx = w / (font_size * atlas.glyph_x);
        dx = 2.0f / Nx;
        dy = dx * atlas.ratio * (float) w / (float) h;
        Ny = (int) (2.0f / dy) + 1;

        cell.x = 0;
        cell.y = 0;

        refresh_mesh();
        mesh.resize(ivec2.of(Nx, Ny), fvec2.of(dx, dy));
    }

    public void writeAt(char chr, ivec2 c) {
        int val = (int) chr;

        int[] data = { val % atlas.Nx, val / atlas.Nx };
        int offset = 8 * (c.x + Nx * c.y); // I32;

        glyph_buffer.subload(offset, data);
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

            // glEnable(GL_CULL_FACE);
            // glCullFace(GL_BACK);
            // glFrontFace(GL_CCW);

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
                glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
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