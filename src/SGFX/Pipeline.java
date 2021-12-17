package src.SGFX;

import java.util.*;
import java.io.IOException;
import java.nio.file.*;
import static org.lwjgl.opengl.GL43C.*;

class Shader extends GL_Object {
    public enum Type {
        FRAGMENT(GL_FRAGMENT_SHADER),
        VERTEX(GL_VERTEX_SHADER);

        public final int gl_type;

        Type(int type) {
            gl_type = type;
        }
    }

    public final String label;

    public Shader(Type type, String src, String label) throws RuntimeException {
        super(glCreateShader(type.gl_type));
        this.label = label;

        glShaderSource(handle, src);
        glCompileShader(handle);

        if (glGetShaderi(handle, GL_COMPILE_STATUS) != GL_TRUE) {
            throw new SGFX_Error(label, "Shader", "Compilation failed", glGetShaderInfoLog(handle));
        }
    }

    public void destroy() {
        glDeleteShader(handle);
    }
    public void attach(int program_handle) {
        glAttachShader(program_handle, handle);
    }
}

class Program extends GL_Bindable {
    public Program(String label, Iterable<Shader> shaders) throws RuntimeException {
        super(glCreateProgram());

        for (Shader shader : shaders) {
            shader.attach(handle);
        }

        glLinkProgram(handle);
        if (glGetProgrami(handle, GL_LINK_STATUS) != GL_TRUE) {
            throw new SGFX_Error(label, "Program", "Linking failed", glGetProgramInfoLog(handle));
        }
        glValidateProgram(handle);
        if (glGetProgrami(handle, GL_VALIDATE_STATUS) != GL_TRUE) {
            throw new SGFX_Error(label, "Program", "Validation failed", glGetProgramInfoLog(handle));
        }
    }

    public void destroy() {
        glDeleteProgram(handle);
    }

    protected void bind_handle(int handle) {
        glUseProgram(handle);
    }
}

public class Pipeline implements Resource, Bindable {
    private Shader frag_shader;
    private Shader vert_shader;
    private Program program;

    private void load(String label, String frag_src, String vert_src) {
        frag_shader = new Shader(Shader.Type.FRAGMENT, frag_src, label + ".frag");
        vert_shader = new Shader(Shader.Type.VERTEX, vert_src, label + ".vert");

        List<Shader> shaders = new ArrayList<>();
        shaders.add(frag_shader);
        shaders.add(vert_shader);

        program = new Program(label, shaders);
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