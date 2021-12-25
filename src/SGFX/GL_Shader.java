package src.SGFX;

import static org.lwjgl.opengl.GL20C.*;

class GL_Shader extends GL_Object {
    public enum Type {
        FRAGMENT(GL_FRAGMENT_SHADER),
        VERTEX(GL_VERTEX_SHADER);

        public final int gl_type;

        Type(int type) {
            gl_type = type;
        }
    }

    public final String label;

    public GL_Shader(Type type, String src, String label) throws RuntimeException {
        super(glCreateShader(type.gl_type));
        this.label = label;

        glShaderSource(gl_handle, src);
        glCompileShader(gl_handle);

        if (glGetShaderi(gl_handle, GL_COMPILE_STATUS) != GL_TRUE) {
            throw new SGFX_Error(label, "Shader", "Compilation failed", glGetShaderInfoLog(gl_handle));
        }
    }

    public void destroy() {
        glDeleteShader(gl_handle);
    }
}