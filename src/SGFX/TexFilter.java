package src.SGFX;

import static org.lwjgl.opengl.GL43C.*;

public class TexFilter {
    public enum MipMap {
        None,
        Linear,
        Nearest;
    }

    public enum Fn {
        Linear(GL_LINEAR),
        Nearest(GL_NEAREST);

        public final int gl_value;

        private Fn(int value) {
            gl_value = value;
        }

        public int min_value(MipMap mode) {
            switch (mode) {
                case None:
                    return gl_value;
                case Linear: {
                    switch (this) {
                        case Linear:
                            return GL_LINEAR_MIPMAP_LINEAR;
                        case Nearest:
                            return GL_LINEAR_MIPMAP_NEAREST;
                    }
                }
                case Nearest: {
                    switch (this) {
                        case Linear:
                            return GL_NEAREST_MIPMAP_LINEAR;
                        case Nearest:
                            return GL_NEAREST_MIPMAP_NEAREST;
                    }
                }
            }
            return gl_value;
        }
    }

    public final int gl_min_filter;
    public final int gl_mag_filter;

    public TexFilter(Fn min, Fn mag, MipMap mode) {
        gl_mag_filter = mag.gl_value;
        gl_min_filter = min.min_value(mode);
    }
}
