package de.twometer.neko.gui;

import com.labymedia.ultralight.plugin.logging.UltralightLogLevel;
import com.labymedia.ultralight.plugin.logging.UltralightLogger;
import de.twometer.neko.util.Log;

public class GuiLogger implements UltralightLogger {

    @Override
    public void logMessage(UltralightLogLevel level, String message) {

        switch (level) {
            case ERROR:
                Log.e(message);
                break;
            case WARNING:
                Log.w(message);
                break;
            case INFO:
                Log.i(message);
                break;
        }

    }

}