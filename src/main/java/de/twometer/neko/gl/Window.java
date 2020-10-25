package de.twometer.neko.gl;

import de.twometer.neko.event.*;
import de.twometer.neko.res.AssetPaths;
import de.twometer.neko.res.ResourceLoader;
import de.twometer.neko.util.Cache;
import de.twometer.neko.util.CrashHandler;
import org.joml.Vector2f;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;

import java.io.IOException;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private long handle;

    private final String title;

    private int width;

    private int height;

    private float scale;

    private final Cache<Integer, Long> cursorCache = new Cache<>() {
        @Override
        protected Long create(Integer integer) {
            return glfwCreateStandardCursor(integer);
        }
    };

    public Window(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
    }

    public void setCursorVisible(boolean visible) {
        glfwSetInputMode(handle, GLFW_CURSOR, visible ? GLFW_CURSOR_NORMAL : GLFW_CURSOR_DISABLED);
    }

    public boolean isCursorVisible() {
        return glfwGetInputMode(handle, GLFW_CURSOR) == GLFW_CURSOR_NORMAL;
    }

    public void create() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("Failed to initialize GLFW");

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        handle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (handle == NULL)
            throw new IllegalStateException("Failed to create GLFW window");

        glfwMakeContextCurrent(handle);
        glfwSwapInterval(1);

        float[] scaleBuf = new float[1];
        glfwGetWindowContentScale(handle, scaleBuf, scaleBuf);
        scale = scaleBuf[0];

        if (scale > 1.0f)
            setSize((int) (width * scale), (int) (height * scale));

        GL.createCapabilities();

        glfwSetFramebufferSizeCallback(handle, (window, width, height) -> {
            this.width = width;
            this.height = height;

            Events.post(new SizeChangedEvent(width, height));
        });

        glfwSetMouseButtonCallback(handle, (window, button, action, mods) -> {
            if (action == GLFW_RELEASE)
                Events.post(new MouseClickedEvent(button));
            Events.post(new MouseButtonEvent(button, action, mods));
        });

        glfwSetKeyCallback(handle, (window, key, scancode, action, mods) -> {
            if (action == GLFW_REPEAT || action == GLFW_PRESS)
                Events.post(new KeyPressedEvent(key));
            Events.post(new KeyEvent(key, scancode, action, mods));
        });
        glfwSetCharCallback(handle, (window, codepoint) -> Events.post(new CharTypedEvent((char) codepoint, codepoint)));
        glfwSetCursorPosCallback(handle, (window, xpos, ypos) -> Events.post(new MousePosEvent((int) xpos, (int) ypos)));
        glfwSetScrollCallback(handle, (window, xoffset, yoffset) -> Events.post(new ScrollEvent((int) xoffset, (int) yoffset)));
        glfwSetWindowFocusCallback(handle, (window, focused) -> Events.post(new WindowFocusEvent(focused)));
    }

    public void update() {
        glfwSwapBuffers(handle);
        glfwPollEvents();
    }

    public void destroy() {
        glfwFreeCallbacks(handle);
        glfwDestroyWindow(handle);

        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    public float getScale() {
        return scale;
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(handle);
    }

    public void close() {
        glfwSetWindowShouldClose(handle, true);
    }

    public void setCursorPosition(Vector2f vec) {
        glfwSetCursorPos(handle, vec.x, vec.y);
    }

    public boolean isKeyPressed(int key) {
        return glfwGetKey(handle, key) == GLFW_PRESS;
    }

    public boolean isMouseButtonPressed(int key) {
        return glfwGetMouseButton(handle, key) == GLFW_PRESS;
    }

    public Vector2f getCursorPosition() {
        double[] xPos = new double[1];
        double[] yPos = new double[1];
        glfwGetCursorPos(handle, xPos, yPos);
        return new Vector2f((float) xPos[0], (float) yPos[0]);
    }

    private void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        glfwSetWindowSize(handle, width, height);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getClipboardContent() {
        return glfwGetClipboardString(handle);
    }

    public void setClipboardContent(String str) {
        glfwSetClipboardString(handle, str);
    }

    public void setCursor(int cursor) {
        var cursorObj = cursor == 0 ? 0 : cursorCache.get(cursor);
        glfwSetCursor(handle, cursorObj);
    }

    public void setIcon(String path) {
        try {
            var img = ResourceLoader.loadImage(AssetPaths.TEXTURE_PATH + path);
            var pix = ResourceLoader.loadPixels(img);
            var buf = GLFWImage.malloc(1);
            var glfwImg = GLFWImage.malloc();
            glfwImg.set(img.getWidth(), img.getHeight(), pix);
            buf.put(0, glfwImg);
            glfwSetWindowIcon(handle, buf);
        } catch (IOException e) {
            CrashHandler.fatal(e);
        }
    }

}
