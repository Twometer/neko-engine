package de.twometer.orion.gui;

import com.labymedia.ultralight.*;
import com.labymedia.ultralight.bitmap.UltralightBitmapSurface;
import com.labymedia.ultralight.config.FontHinting;
import com.labymedia.ultralight.config.UltralightConfig;
import com.labymedia.ultralight.math.IntRect;
import de.twometer.orion.core.OrionApp;
import de.twometer.orion.event.Events;
import de.twometer.orion.event.SizeChangedEvent;
import de.twometer.orion.render.model.Mesh;
import de.twometer.orion.render.model.ModelBase;
import de.twometer.orion.render.shading.IShadingStrategy;
import de.twometer.orion.res.AssetPaths;
import de.twometer.orion.util.CrashHandler;
import de.twometer.orion.util.Log;
import org.greenrobot.eventbus.Subscribe;
import org.joml.Matrix4f;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

public class GuiManager {

    private I18n i18n = new I18n();

    private Matrix4f guiMatrix;

    private UltralightPlatform platform;
    private UltralightRenderer renderer;
    private UltralightView view;

    private int texture = -1;

    private GuiShader guiShader;
    private ModelBase quadModel;

    public void create() {
        Log.d("Initializing gui system");
        i18n.load();

        var ultralightPath = AssetPaths.NATIVE_PATH + "Ultralight/";
        var resourcePath = ultralightPath + "resources/";
        var nativesDir = Path.of(ultralightPath);
        try {
            if (!Files.exists(nativesDir))
                throw new UltralightLoadException("Native ultralight libraries not found. Make sure you have the latest ultralight libraries in " + ultralightPath);

            UltralightJava.extractNativeLibrary(nativesDir);
            UltralightJava.load(nativesDir);
        } catch (UltralightLoadException e) {
            CrashHandler.fatal(e);
        }

        var window = OrionApp.get().getWindow();
        platform = UltralightPlatform.instance();
        platform.setConfig(
                new UltralightConfig()
                        .resourcePath(resourcePath)
                        .fontHinting(FontHinting.NORMAL)
                        .deviceScale(window.getScale())
        );
        platform.usePlatformFileSystem(AssetPaths.GUI_PATH);
        platform.usePlatformFontLoader();
        platform.setLogger(new GuiLogger());
        platform.setClipboard(new GuiClipboard());

        renderer = UltralightRenderer.create();
        renderer.logMemoryUsage();

        view = renderer.createView(window.getWidth(), window.getHeight(), true);

        quadModel = Mesh.create(4, 2)
                .putVertex(0, 0)
                .putVertex(1, 0)
                .putVertex(0, 1)
                .putVertex(1, 1)
                .bake("GuiQuad", GL_TRIANGLE_STRIP);

        guiShader = OrionApp.get().getShaderProvider().getShader(GuiShader.class);

        Events.register(this);

        Log.i("Gui System initialized");
    }

    public void showPage(String path) {
        var url = "file:///" + path.replace("\\", "/");
        Log.i("Navigating to " + url);
        this.view.loadURL(url);
    }

    public void render() {
        if (texture == -1) genTexture();

        renderer.update();
        renderer.render();

        // We wanna do our own shading here
        OrionApp.get().getRenderManager().setShadingStrategy(IShadingStrategy.NOP);

        var surface = (UltralightBitmapSurface) view.surface();
        var bitmap = surface.bitmap();

        var width = (int) view.width();
        var height = (int) view.height();

        glBindTexture(GL_TEXTURE_2D, texture);
        IntRect dirtyBounds = surface.dirtyBounds();

        if (dirtyBounds.isValid()) {
            ByteBuffer imageData = bitmap.lockPixels();

            glPixelStorei(GL_UNPACK_ROW_LENGTH, (int) bitmap.rowBytes() / 4);
            if (dirtyBounds.width() == width && dirtyBounds.height() == height) {
                // Update full image
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, imageData);
                glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
            } else {
                // Update partial image
                int x = dirtyBounds.x();
                int y = dirtyBounds.y();
                int dirtyWidth = dirtyBounds.width();
                int dirtyHeight = dirtyBounds.height();
                int startOffset = (int) ((y * bitmap.rowBytes()) + x * 4);

                glTexSubImage2D(
                        GL_TEXTURE_2D,
                        0,
                        x, y, dirtyWidth, dirtyHeight,
                        GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV,
                        imageData.position(startOffset));
            }
            glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);

            bitmap.unlockPixels();
            surface.clearDirtyBounds();
        }

        guiShader.bind();
        guiShader.guiMatrix.set(guiMatrix);
        quadModel.render();
    }

    @Subscribe
    public void onSizeChanged(SizeChangedEvent e) {
        view.resize(e.width, e.height);
        guiMatrix = new Matrix4f().ortho2D(0, 1.0f, 1.0f, 0);
    }

    private void genTexture() {
        Log.d("Creating GUI texture");
        texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    }

}
