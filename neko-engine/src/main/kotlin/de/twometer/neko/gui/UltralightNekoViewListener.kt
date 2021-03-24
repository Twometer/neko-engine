package de.twometer.neko.gui

import com.labymedia.ultralight.UltralightView
import com.labymedia.ultralight.input.UltralightCursor
import com.labymedia.ultralight.math.IntRect
import com.labymedia.ultralight.plugin.view.MessageLevel
import com.labymedia.ultralight.plugin.view.MessageSource
import com.labymedia.ultralight.plugin.view.UltralightViewListener
import de.twometer.neko.core.NekoApp
import mu.KotlinLogging
import org.lwjgl.glfw.GLFW.*

private val logger = KotlinLogging.logger {}

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
        val formatted = "[JavaScript] [$sourceId] [$lineNumber:$columnNumber] $message"
        when (level) {
            MessageLevel.DEBUG -> logger.debug { formatted }
            MessageLevel.INFO -> logger.info { formatted }
            MessageLevel.LOG -> logger.info { formatted }
            MessageLevel.WARNING -> logger.warn { formatted }
            MessageLevel.ERROR -> logger.error { formatted }
        }
    }

    override fun onCreateChildView(
        openerUrl: String?,
        targetUrl: String?,
        isPopup: Boolean,
        popupRect: IntRect?
    ): UltralightView? = null

}