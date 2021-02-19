package de.twometer.neko.core

import de.twometer.neko.Neko
import de.twometer.neko.events.Events
import mu.KotlinLogging
import org.lwjgl.opengl.GL11.*

private val logger = KotlinLogging.logger {}

open class NekoApp(config: AppConfig) {

    protected val window = Window(config)

    fun run() {
        logger.info { "Starting Neko Engine v${Neko.VERSION}" }
        Events.setup()
        window.create()

        val version = glGetString(GL_VERSION)
        val vendor = glGetString(GL_VENDOR)
        logger.info { "Detected OpenGL $version ($vendor)" }

        while (!window.isCloseRequested()) {


            window.update()
        }

        logger.info { "Shutting down..." }
        window.destroy()
    }

}