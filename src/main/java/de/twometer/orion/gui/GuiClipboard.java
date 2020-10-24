package de.twometer.orion.gui;

import com.labymedia.ultralight.plugin.clipboard.UltralightClipboard;
import de.twometer.orion.core.OrionApp;

public class GuiClipboard implements UltralightClipboard {

    @Override
    public void clear() {
        OrionApp.get().getWindow().setClipboardContent("");
    }

    @Override
    public String readPlainText() {
        return OrionApp.get().getWindow().getClipboardContent();
    }

    @Override
    public void writePlainText(String text) {
        OrionApp.get().getWindow().setClipboardContent(text);
    }

}
