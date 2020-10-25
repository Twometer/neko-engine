package de.twometer.neko.gui;

import com.labymedia.ultralight.plugin.clipboard.UltralightClipboard;
import de.twometer.neko.core.NekoApp;

public class GuiClipboard implements UltralightClipboard {

    @Override
    public void clear() {
        NekoApp.get().getWindow().setClipboardContent("");
    }

    @Override
    public String readPlainText() {
        return NekoApp.get().getWindow().getClipboardContent();
    }

    @Override
    public void writePlainText(String text) {
        NekoApp.get().getWindow().setClipboardContent(text);
    }

}
