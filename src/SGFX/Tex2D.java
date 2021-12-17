package src.SGFX;

import java.nio.ByteBuffer;
import org.lwjgl.stb.STBImage;

import static org.lwjgl.opengl.GL43C.*;


public class Tex2D extends Tex {
    private int[] size;

    public int[] getSize() {
        return size;
    }

    public Tex2D() {
        super(Tex.Dim.D2);
        size = new int[2];
    }

    public void load(String path, Boolean mipmap) {
        int ptr_w[] = { 0 };
        int ptr_h[] = { 0 };
        int channels[] = { 0 };

        // STBImage.stbi_set_flip_vertically_on_load(true);
        ByteBuffer buff = STBImage.stbi_load(path, ptr_w, ptr_h, channels, 3);
        size[0] = ptr_w[0];
        size[1] = ptr_h[0];

        bind();
        glTexImage2D(dim.gl_value, 0, GL_RGB, size[0], size[1], 0, GL_RGB, GL_UNSIGNED_BYTE, buff);
        if (mipmap) {
            glGenerateMipmap(dim.gl_value);
        }
        unbind();

        STBImage.stbi_image_free(buff);
    }
}
