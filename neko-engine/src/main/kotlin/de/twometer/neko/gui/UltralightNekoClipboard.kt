package de.twometer.neko.gui

import com.labymedia.ultralight.plugin.clipboard.UltralightClipboard
import de.twometer.neko.core.NekoApp

class UltralightNekoClipboard : UltralightClipboard {

    override fun clear() = NekoApp.the.window.setClipboardContent("")

    override fun readPlainText(): String = NekoApp.the.window.getClipboardContent() ?: ""

    override fun writePlainText(text: String?) {
        if (text != null) NekoApp.the.window.setClipboardContent(text)
    }

}