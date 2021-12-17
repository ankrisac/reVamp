package src;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.Callbacks;

import static org.lwjgl.system.MemoryUtil.NULL;

class Window {
    public final long handle;

    public Window(int w, int h, String title) {
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, GLFW.GLFW_TRUE);

        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE,
                GLFW.GLFW_OPENGL_CORE_PROFILE);

        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);

        handle = GLFW.glfwCreateWindow(w, h, title, NULL, NULL);
        if (handle == NULL) {
            throw new RuntimeException("Unable to create window");
        }

    }

    public void destroy() {
        Callbacks.glfwFreeCallbacks(handle);
        GLFW.glfwDestroyWindow(handle);
    }

    public void context_focus() {
        GLFW.glfwMakeContextCurrent(handle);
        GLFW.glfwSwapInterval(1);
    }

    public int[] getSize() {
        int[] w = { 0 };
        int[] h = { 0 };
        GLFW.glfwGetWindowSize(handle, w, h);

        int[] dim = { w[0], h[0] };
        return dim;
    }

    public boolean is_open() {
        return !GLFW.glfwWindowShouldClose(handle);
    }

    public void swap() {
        GLFW.glfwSwapBuffers(handle);
    }
}
