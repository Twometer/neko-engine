package de.twometer.neko.gui

import com.labymedia.ultralight.plugin.logging.UltralightLogLevel
import com.labymedia.ultralight.plugin.logging.UltralightLogger
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class UltralightNekoLogger : UltralightLogger {

    override fun logMessage(level: UltralightLogLevel?, message: String?) {
        when (level) {
            UltralightLogLevel.INFO -> logger.info { message }
            UltralightLogLevel.WARNING -> logger.warn { message }
            UltralightLogLevel.ERROR -> logger.error { message }
        }
    }

}