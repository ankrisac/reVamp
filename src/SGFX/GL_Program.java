package src.SGFX;

import static org.lwjgl.opengl.GL20C.*;

class GL_Program extends GL_Bindable {
    public final String label;

    public GL_Program(String label) {
        super(glCreateProgram());
        this.label = label;
    }

    public void compile() throws RuntimeException {
        glLinkProgram(gl_handle);
        if (glGetProgrami(gl_handle, GL_LINK_STATUS) != GL_TRUE) {
            throw new SGFX_Error(label, "Program", "Linking failed", glGetProgramInfoLog(gl_handle));
        }
        glValidateProgram(gl_handle);
        if (glGetProgrami(gl_handle, GL_VALIDATE_STATUS) != GL_TRUE) {
            throw new SGFX_Error(label, "Program", "Validation failed", glGetProgramInfoLog(gl_handle));
        }

    }

    public void attach(GL_Shader shader) {
        glAttachShader(gl_handle, shader.gl_handle);
    }

    public void destroy() {
        glDeleteProgram(gl_handle);
    }

    protected void bind_handle(int handle) {
        glUseProgram(handle);
    }
}