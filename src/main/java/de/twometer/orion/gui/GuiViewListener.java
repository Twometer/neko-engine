package de.twometer.orion.gui;

import com.labymedia.ultralight.UltralightView;
import com.labymedia.ultralight.input.UltralightCursor;
import com.labymedia.ultralight.math.IntRect;
import com.labymedia.ultralight.plugin.view.MessageLevel;
import com.labymedia.ultralight.plugin.view.MessageSource;
import com.labymedia.ultralight.plugin.view.UltralightViewListener;
import de.twometer.orion.core.OrionApp;
import de.twometer.orion.gl.Window;
import de.twometer.orion.util.Log;

import static org.lwjgl.glfw.GLFW.*;

public class GuiViewListener implements UltralightViewListener {

    private final Window window;

    public GuiViewListener() {
        window = OrionApp.get().getWindow();
    }

    @Override
    public void onChangeTitle(String title) {

    }

    @Override
    public void onChangeURL(String url) {

    }

    @Override
    public void onChangeTooltip(String tooltip) {

    }

    @Override
    public void onChangeCursor(UltralightCursor cursor) {
        switch(cursor) {
            case CROSS:
                window.setCursor(GLFW_CROSSHAIR_CURSOR);
                break;
            case HAND:
                window.setCursor(GLFW_HAND_CURSOR);
                break;
            case I_BEAM:
                window.setCursor(GLFW_IBEAM_CURSOR);
                break;
            case EAST_WEST_RESIZE:
                window.setCursor(GLFW_HRESIZE_CURSOR);
                break;
            case NORTH_SOUTH_RESIZE:
                window.setCursor(GLFW_VRESIZE_CURSOR);
                break;
            default:
                window.setCursor(0);
                break;
        }
    }

    @Override
    public void onAddConsoleMessage(MessageSource source, MessageLevel level, String message, long lineNumber, long columnNumber, String sourceId) {
        var formatted = String.format("[JavaScript] [%s] [%d:%d] %s", sourceId, lineNumber, columnNumber, message);
        switch (level) {
            case ERROR:
                Log.e(formatted);
                break;
            case DEBUG:
                Log.d(formatted);
                break;
            case INFO:
            case LOG:
                Log.i(formatted);
                break;
            case WARNING:
                Log.w(formatted);
                break;
        }
    }

    @Override
    public UltralightView onCreateChildView(String openerUrl, String targetUrl, boolean isPopup, IntRect popupRect) {
        return null;
    }
}
