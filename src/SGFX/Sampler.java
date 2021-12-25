package src.SGFX;

import static org.lwjgl.opengl.GL33C.*;

public class Sampler extends GL_Object {
    public Sampler(GL_Tex.Dim dim) {
        super(glGenSamplers());
    }

    public Sampler border(float color[]) {
        glSamplerParameterfv(gl_handle, GL_TEXTURE_BORDER_COLOR, color);
        return this;
    }

    public Sampler wrap(TexWrap mode) {
        if (mode.r != null)
            setParam(GL_TEXTURE_WRAP_R, mode.r.gl_value);
        if (mode.s != null)
            setParam(GL_TEXTURE_WRAP_S, mode.s.gl_value);
        if (mode.t != null)
            setParam(GL_TEXTURE_WRAP_T, mode.t.gl_value);
        return this;
    }

    public Sampler filter(TexFilter filter) {
        setParam(GL_TEXTURE_MIN_FILTER, filter.gl_min_filter);
        setParam(GL_TEXTURE_MAG_FILTER, filter.gl_mag_filter);
        return this;
    }

    public void destroy() {
        glDeleteSamplers(gl_handle);
    }

    public void setParam(int param, int value) {
        glSamplerParameteri(gl_handle, param, value);
    }

    public void bind(int index) {
        glBindSampler(index, gl_handle);
    }
}
