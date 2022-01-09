package src;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.Callbacks;

import src.SGFX.*;
import src.SGFX.Vec.*;
import static org.lwjgl.system.MemoryUtil.NULL;

class Window implements Resource {
    public final long handle;
    public final i32x2 size;

    public Window(i32x2 size, String title) {
        this.size = size;

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_DOUBLEBUFFER, GLFW.GLFW_TRUE);

        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);

        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);

        handle = GLFW.glfwCreateWindow(size.x, size.y, title, NULL, NULL);
        if (handle == NULL) {
            throw new RuntimeException("Unable to create window");
        }
    }

    public f32x2 px() {
        return f32x2.of(2f / size.x, 2f / size.y);
    }

    public f32x2 px(float x, float y) {
        return px().mul(x, y);
    }
    public f32x2 px(f32x2 val) {
        return px().mul(val.x, val.y);
    }

    public void destroy() {
        Callbacks.glfwFreeCallbacks(handle);
        GLFW.glfwDestroyWindow(handle);
    }

    public void context_focus() {
        GLFW.glfwMakeContextCurrent(handle);
    }

    public boolean is_open() {
        return !GLFW.glfwWindowShouldClose(handle);
    }

    public void swap() {
        GLFW.glfwSwapBuffers(handle);
    }
}
