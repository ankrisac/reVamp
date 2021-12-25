package src.SGFX;

import java.nio.ByteBuffer;
import org.lwjgl.stb.STBImage;

import src.SGFX.Vec.*;
import static org.lwjgl.opengl.GL30C.*;

public class Tex2D extends GL_Tex {
    private i32x2 size;

    public i32x2 getSize() {
        return size;
    }

    public Tex2D() {
        super(GL_Tex.Dim.D2);
        size = i32x2.of(0, 0);
    }

    public void load(String path, Boolean mipmap) {
        int ptr_w[] = { 0 };
        int ptr_h[] = { 0 };
        int channels[] = { 0 };

        // STBImage.stbi_set_flip_vertically_on_load(true);
        ByteBuffer buff = STBImage.stbi_load(path, ptr_w, ptr_h, channels, 3);
        size.x = ptr_w[0];
        size.y = ptr_h[0];

        bind();
        glTexImage2D(dim.gl_value, 0, GL_RGB, size.x, size.y, 0, GL_RGB, GL_UNSIGNED_BYTE, buff);
        if (mipmap) {
            glGenerateMipmap(dim.gl_value);
        }
        unbind();

        STBImage.stbi_image_free(buff);
    }
}
