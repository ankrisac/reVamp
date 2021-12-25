package src.SGFX;

import java.io.IOException;
import java.nio.file.*;

public class Pipeline implements Resource, Bindable {
    private GL_Shader frag_shader;
    private GL_Shader vert_shader;
    private GL_Program program;

    private void load(String label, String frag_src, String vert_src) {
        frag_shader = new GL_Shader(GL_Shader.Type.FRAGMENT, frag_src, label + ".frag");
        vert_shader = new GL_Shader(GL_Shader.Type.VERTEX, vert_src, label + ".vert");

        program = new GL_Program(label);
        program.attach(frag_shader);
        program.attach(vert_shader);
        program.compile();
    }

    public Pipeline(String label, String frag_src, String vert_src) {
        load(label, frag_src, vert_src);
    }

    public Pipeline(String label, String path) throws IOException {
        String frag_src = new String(Files.readAllBytes(Paths.get(path + ".frag")));
        String vert_src = new String(Files.readAllBytes(Paths.get(path + ".vert")));

        load(label, frag_src, vert_src);
    }

    public void destroy() {
        frag_shader.destroy();
        vert_shader.destroy();
        program.destroy();
    }

    public void bind() {
        program.bind();
    }

    public void unbind() {
        program.unbind();
    }
}