package src;

import static org.lwjgl.opengl.GL43C.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;

import src.SGFX.*;
import src.SGFX.Vec.*;

class Font implements Resource, Bindable {
    public final Tex2D atlas = new Tex2D();
    public final Sampler sampler = new Sampler(GL_Tex.Dim.D2);

    public final i32x2 glyph_N;
    public final i32x2 glyph_dim;

    public Font() {
        atlas.load("assets/Termfont.png", false);
        sampler.filter(new TexFilter(TexFilter.Fn.Nearest, TexFilter.Fn.Nearest, TexFilter.MipMap.None));
        sampler.wrap(new TexWrap(TexWrap.Axis.Repeat, TexWrap.Axis.Repeat));
        sampler.bind(0);

        glyph_N = i32x2.of(16, 8);
        glyph_dim = atlas.getSize().div(glyph_N);
    }

    public void destroy() {
        Resource.destroy(atlas, sampler);
    }

    public void bind() {
        atlas.bind();
        sampler.bind(0);
    }

    public void unbind() {
        atlas.unbind();
    }

    public void getGlyph(f32x2 px_size, int codepoint, f32x2 out_dim, f32x2 out_uv_pos, f32x2 out_uv_dim) {
        if (codepoint >= 128) {
            throw new RuntimeException("Multilingual code points not supported!");
        }

        out_dim.set(glyph_dim.f()).mul(px_size);

        f32x2 N = glyph_N.f();
        out_uv_pos.set(codepoint % 16, codepoint / 16).div(N);
        out_uv_dim.set(1, 1).div(N);
    }
}

class Style {
    public float font_size = 1;
    public float inner_padding = 0;
    
    public final f32x4 back = f32x4.of(1, 1, 1, 1);
    public final f32x4 fore = f32x4.of(0, 0, 0, 1);

    public final f32x2 pad = f32x2.of(0, 0);

    public Style() {
    }

    public Style(Style other) {
        this.set(other);
    }

    public void set(Style other) {
        this.font_size = other.font_size;
        this.inner_padding = 0;

        this.back.set(other.back);
        this.fore.set(other.fore);
        this.pad.set(other.pad);
    }
}

class UIBuilder implements Resource {
    public final Font font = new Font();

    private final int initial_size = 1024;
    public final u32x1.Index ibo = new u32x1.Index(BufFmt.Usage.StreamDraw, initial_size);
    public final f32x3.Attrib vert = new f32x3.Attrib(BufFmt.Usage.StreamDraw, initial_size);
    public final f32x2.Attrib uv = new f32x2.Attrib(BufFmt.Usage.StreamDraw, initial_size);
    public final f32x4.Attrib colback = new f32x4.Attrib(BufFmt.Usage.StreamDraw, initial_size);
    public final f32x4.Attrib colfore = new f32x4.Attrib(BufFmt.Usage.StreamDraw, initial_size);

    private boolean realloced = true;

    public final BindGroup layout = new BindGroup();
    public Pipeline pipeline;

    private int ibo_index = 0;
    private int attrib_index = 0;

    public final Window parent;

    public UIBuilder(Window parent) {
        this.parent = parent;

        try {
            pipeline = new Pipeline("ui-builder", "shaders/glyph");
        } catch (Exception ex) {
            System.out.println("Error building UIBuilder pipeline:\n" + ex.getMessage());
            System.exit(1);
        }

        layout.attach(0, vert);
        layout.attach(1, uv);
        layout.attach(2, colback);
        layout.attach(3, colfore);
    }

    public void destroy() {
        Resource.destroy(pipeline, layout, ibo, vert, uv, font);
    }

    public void start() {
        ibo_index = 0;
        attrib_index = 0;
    }

    public void build() {
        ibo.update(realloced);
        vert.update(realloced);
        uv.update(realloced);
        colback.update(realloced);
        colfore.update(realloced);
    }

    public void draw() {
        Bindable.bind(pipeline, font, vert, uv);
        layout.draw(BindGroup.DrawMode.Tris, ibo, ibo_index);
    }

    private int grow(int size, int min_size) {
        if (size <= min_size) {
            while (size < min_size) {
                size <<= 1;
            }
            return size;
        }
        return 0;
    }

    private void extend(int index, int attrib) {
        int ibo_len = grow(ibo.local.size(), ibo_index + index);
        if (ibo_len > 0) {
            ibo.local.resize(ibo_len);
        }

        int attrib_len = grow(vert.local.size(), attrib_index + attrib);
        if (attrib_len > 0) {
            vert.local.resize(attrib_len);
            uv.local.resize(attrib_len);
            colback.local.resize(attrib_len);
            colfore.local.resize(attrib_len);
        }

        realloced = true;
    }

    public void box(f32x3 pos, f32x2 dim, Style style) {
        extend(6, 4);

        int indices[] = { 0, 1, 2, 0, 2, 3 };

        for (int i : indices) {
            ibo.local.set(ibo_index++, attrib_index + i);
        }

        f32x2[] offsets = {
                f32x2.of(0, 0),
                f32x2.of(0, 1),
                f32x2.of(1, 1),
                f32x2.of(1, 0)
        };

        for (f32x2 off : offsets) {
            vert.local.set(attrib_index, pos.cadd(dim.x * off.x, -dim.y * off.y, 0));

            uv.local.set(attrib_index, f32x2.zero());
            colback.local.set(attrib_index, style.back);
            colfore.local.set(attrib_index, style.fore);
            attrib_index++;
        }
    }

    public BoxSize text(String text, f32x3 pos, f32x2 max_dim, Style style) {
        int[] codepoint_array = text.codePoints().toArray();
        extend(6 * codepoint_array.length, 4 * codepoint_array.length);

        BoxSize size = new BoxSize(f32x2.zero(), max_dim);

        int num_glyphs = 0;
        int attrib_begin = attrib_index;

        {
            f32x2 uv_pos = f32x2.zero();
            f32x2 uv_dim = f32x2.zero();

            f32x2 glyph_dim = f32x2.zero();
            f32x3 glyph_pos = pos.copy();

            f32x2[] offset = {
                    f32x2.of(0, 0),
                    f32x2.of(0, 1),
                    f32x2.of(1, 1),
                    f32x2.of(1, 0),
            };

            for (int codepoint : codepoint_array) {
                if (codepoint == '\n') {
                    glyph_pos.x = pos.x;
                    glyph_pos.y -= glyph_dim.y;
                    continue;
                }

                font.getGlyph(parent.px().mul(style.font_size, style.font_size), codepoint, glyph_dim, uv_pos,
                        uv_dim);

                if (glyph_pos.x >= pos.x + max_dim.x) {
                    glyph_pos.x = pos.x;
                    glyph_pos.y -= glyph_dim.y;

                    size.min_dim.x = Math.max(size.min_dim.x, glyph_pos.x - pos.x);
                }
                if (glyph_pos.y <= pos.y - max_dim.y) {
                    break;
                }

                for (f32x2 off : offset) {
                    vert.local.set(attrib_index, glyph_pos.cadd(off.x * glyph_dim.x, -off.y * glyph_dim.y, 0));

                    uv.local.set(attrib_index, uv_dim.cmul(off).add(uv_pos));
                    colback.local.set(attrib_index, style.back);
                    colfore.local.set(attrib_index, style.fore);
                    attrib_index++;
                }

                glyph_pos.x += glyph_dim.x;
                num_glyphs++;
            }

            glyph_pos.y -= glyph_dim.y;
            size.min_dim.x = Math.max(size.min_dim.x, glyph_pos.x - pos.x);
            size.min_dim.y = pos.y - glyph_pos.y;
        }

        {
            int[] glyph_indices = { 0, 1, 2, 0, 2, 3 };
            for (int i = 0; i < num_glyphs; i++) {
                for (int index : glyph_indices) {
                    ibo.local.set(ibo_index++, attrib_begin + index);
                }
                attrib_begin += 4;
            }
        }

        return size;
    }
}

class Profiler {
    public double last;
    public double[] time;
    private int index = 0;

    public Profiler(int N) {
        time = new double[N];
    }

    public void push_frame() {
        double now = GLFW.glfwGetTime();
        time[index] = now - last;
        last = now;

        index = (index + 1) % time.length;
    }

    public String getStats() {
        double sum = 0;
        double max = time[0];
        double min = time[0];

        for (int i = 0; i < time.length; i++) {
            sum += time[i];
            max = Math.max(max, time[i]);
            min = Math.min(min, time[i]);
        }

        double avg = sum / time.length;
        return String.format("SPF %.5f [%.5f,%.5f] : FPS %3.2f", avg, min, max, 1f / avg);
    }
}

class BoxSize {
    public f32x2 min_dim;
    public f32x2 max_dim;

    public BoxSize(f32x2 min, f32x2 max) {
        min_dim = min.copy();
        max_dim = max.copy();
    }

    public static BoxSize of(f32x2 min, f32x2 max) {
        return new BoxSize(min, max);
    }

    public static BoxSize Min(f32x2 min) {
        return new BoxSize(min, f32x2.of(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY));
    }
    public static BoxSize Max(f32x2 max) {
        return new BoxSize(f32x2.zero(), max);
    }

    public static BoxSize Min(float x, float y) {
        return new BoxSize(f32x2.of(x, y), f32x2.of(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY));
    }
    public static BoxSize Max(float x, float y) {
        return new BoxSize(f32x2.zero(), f32x2.of(x, y));
    }
}

abstract class Layout {
    public final Style style;
    public final Root root;
    public final Layout parent;

    public Layout(Layout parent) {
        if (parent == null) {
            throw new RuntimeException("parent cannot be null");
        }

        this.style = new Style(parent.style);
        this.parent = parent;
        this.root = parent.root;
    }

    public Layout(Style root_style) {
        this.style = root_style;
        this.parent = null;
        this.root = (Root) this;
    }

    abstract public BoxSize build();
}

class Root extends Layout implements Resource {
    public final Window win;
    public final UIBuilder builder;

    public Root(Window win, Style style) {
        super(style);
        this.win = win;
        this.builder = new UIBuilder(win);
    }

    public void destroy() {
        this.builder.destroy();
    }

    public BoxSize build() {
        this.builder.build();
        return BoxSize.of(f32x2.of(0, 0), win.size.f());
    }

    public void draw() {
        this.builder.draw();
    }
}

abstract class AutoLayout extends Layout {
    public final f32x3 origin;
    public final BoxSize dim;
    
    public AutoLayout(Layout parent, f32x3 origin, BoxSize dim) {
        super(parent);
        this.origin = origin;
        this.dim = dim;
    }

    abstract public f32x3 nextPos();
    abstract public f32x2 nextDim();
    abstract public void packElem(f32x2 size);

    public void text(String text) {
        addSpacer(root.builder.text(text, nextPos(), nextDim(), style));
    }

    public void addSpacer(BoxSize elem) {
        packElem(elem.min_dim);
    }

    public BoxSize build() {
        dim.min_dim.add(style.pad.csmul(2));

        root.builder.box(origin.cadd(0, 0, 0), dim.min_dim, style);
        return this.dim;
    }
}
class Row extends AutoLayout {
    private float offset = 0;
    public int num_elem = 0;

    Row(Layout parent, f32x3 origin, BoxSize dim) {
        super(parent, origin, dim);
    }

    public f32x3 nextPos() {
        num_elem++;
        if(num_elem > 1) {
            offset += style.inner_padding;
        }
        return origin.cadd(style.pad.x, -(style.pad.y + offset), 0.02f);
    }

    public f32x2 nextDim() {
        return dim.max_dim.csub(2 * style.pad.x, offset + 2 * style.pad.y);
    }

    public void packElem(f32x2 size) {
        offset += size.y;
        dim.min_dim.max(size.x, offset);
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

            Window win;
            Root root;
            {
                i32x2 win_size = i32x2.val(500);
                win = new Window(win_size, "UI Test");
                win.context_focus();
                GL.createCapabilities();

                root = new Root(win, new Style());
            }

            glEnable(GL_DEPTH_TEST);
            glDepthFunc(GL_LESS);

            //glEnable(GL_CULL_FACE);
            //glCullFace(GL_BACK);
            //glFrontFace(GL_CCW);
            
            GLFW.glfwSetWindowSizeCallback(win.handle, (window, w, h) -> {
                glViewport(0, 0, w, h);
                win.size.set(w, h);
            });
            GLFW.glfwSetCharCallback(win.handle, (window, codepoint) -> {});

            GLFW.glfwSetKeyCallback(win.handle, (window, key, scancode, action, mod) -> {
                if (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT) {
                    switch (key) {
                        case GLFW.GLFW_KEY_ESCAPE: {
                            toggle_fill();
                            break;
                        }
                        default:
                            break;
                    }
                }
            });

            StringBuilder text = new StringBuilder();
            for (int i = 0; i < 3000; i++) {
                text.append((char) (Math.random() * 128));
            }

            Profiler prof = new Profiler(100);

            String src = "File not found!";
            try {
                Path path = Paths.get("src", "Main.java");
                src = new String(Files.readAllBytes(path));
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

            while (win.is_open()) {
                prof.push_frame();

                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

                for (int i = 0; i < text.length(); i++) {
                    char rand = (char) (int) (Math.random() * 128);
                    if (Math.random() < 0.2) {
                        rand = ' ';
                    }
                    text.setCharAt(i, rand);
                }

                root.builder.start();

                f32x2 px = win.px();

                AutoLayout row = new Row(root, f32x3.of(0, 0, 0), BoxSize.Max(f32x2.of(0.5f, 0.5f)));
                row.style.pad.set(px.smul(4));
                row.style.inner_padding = px.y;
                row.text("hi");
                row.text("hello");
                {
                    AutoLayout inner = new Row(row, row.nextPos(), BoxSize.Max(row.nextDim()));
                    inner.style.back.set(0.7f, 0.7f, 0.7f, 1);
                    inner.style.pad.set(px.csmul(4));
                    inner.style.inner_padding = px.y;
                    inner.text("AutoLayout inner = new Row(row, row.nextPos(), BoxSize.Max(row.nextDim()))\ninner.style.back.set(0.7f, 0.7f, 0.7f, 1);\ninner.style.pad.set(px.csmul(4));\ninner.style.inner_padding = px.y;\ninner.text(\"inner!\");\ninner.text(\"TEXT!\");\nrow.addSpacer(inner.build());");
                    inner.text("TEXT!");
                    row.addSpacer(inner.build());
                }
                row.text("world!");
                row.build();

                root.builder.build();
                root.builder.draw();

                win.swap();
                GLFW.glfwPollEvents();
            }

            Resource.destroy(root, win);
        } catch (Exception ex) {
            System.out.println("[Caught Exception]:\n" + ex.getMessage());
            throw ex;
        }

        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }
}