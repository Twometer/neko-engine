package de.twometer.neko.util

import de.twometer.neko.core.NekoApp
import mu.KotlinLogging
import javax.swing.JOptionPane
import javax.swing.UIManager
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

object CrashHandler {

    fun register() {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        Thread.currentThread().setUncaughtExceptionHandler { _, e -> handleCrash(e) }
    }

    private fun handleCrash(error: Throwable): Nothing {
        logger.error(error) { "A fatal error occurred!" }

        NekoApp.the.window.destroy()

        JOptionPane.showMessageDialog(
            null,
            "<html><h1 style='margin-top:0'>An internal error has occurred</h1><p>Unfortunately, the game has crashed.</p><br>Error message:<p style='font-family: monospaced;'>${error.message}</p><br><p>See the logfile for more details</p><br>",
            "Neko Engine: Internal Error",
            JOptionPane.ERROR_MESSAGE
        )

        exitProcess(1)
    }

}