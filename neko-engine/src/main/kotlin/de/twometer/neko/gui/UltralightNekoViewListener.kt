package de.twometer.neko.gui

import com.labymedia.ultralight.UltralightView
import com.labymedia.ultralight.input.UltralightCursor
import com.labymedia.ultralight.math.IntRect
import com.labymedia.ultralight.plugin.view.MessageLevel
import com.labymedia.ultralight.plugin.view.MessageSource
import com.labymedia.ultralight.plugin.view.UltralightViewListener
import de.twometer.neko.core.NekoApp
import org.lwjgl.glfw.GLFW.*

class UltralightNekoViewListener : UltralightViewListener {

    private val window = NekoApp.the!!.window

    override fun onChangeTitle(title: String?) = Unit

    override fun onChangeURL(url: String?) = Unit

    override fun onChangeTooltip(tooltip: String?) = Unit

    override fun onChangeCursor(cursor: UltralightCursor?) {
        when (cursor) {
            UltralightCursor.CROSS -> window.setCursor(GLFW_CROSSHAIR_CURSOR)
            UltralightCursor.HAND -> window.setCursor(GLFW_HAND_CURSOR)
            UltralightCursor.I_BEAM -> window.setCursor(GLFW_IBEAM_CURSOR)
            UltralightCursor.EAST_WEST_RESIZE -> window.setCursor(GLFW_HRESIZE_CURSOR)
            UltralightCursor.NORTH_SOUTH_RESIZE -> window.setCursor(GLFW_VRESIZE_CURSOR)
            else -> window.setCursor(0)
        }
    }

    override fun onAddConsoleMessage(
        source: MessageSource?,
        level: MessageLevel?,
        message: String?,
        lineNumber: Long,
        columnNumber: Long,
        sourceId: String?
    ) {
        TODO("Not yet implemented")
    }

    override fun onCreateChildView(
        openerUrl: String?,
        targetUrl: String?,
        isPopup: Boolean,
        popupRect: IntRect?
    ): UltralightView? = null

}