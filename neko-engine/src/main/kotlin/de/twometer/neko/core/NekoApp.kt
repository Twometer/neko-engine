package de.twometer.neko.core

import de.twometer.neko.Neko
import de.twometer.neko.events.Events
import de.twometer.neko.res.ShaderLoader
import de.twometer.neko.util.Timer
import mu.KotlinLogging
import org.lwjgl.opengl.GL11.*

private val logger = KotlinLogging.logger {}

open class NekoApp(config: AppConfig) {

    protected val window = Window(config)
    protected val timer = Timer(config.timerSpeed)

    fun run() {
        logger.info { "Starting Neko Engine v${Neko.VERSION}" }
        Events.setup()

        onPreInit()

        window.create()

        val version = glGetString(GL_VERSION)
        val vendor = glGetString(GL_VENDOR)
        logger.info { "Detected OpenGL $version ($vendor)" }

        onPostInit()

        ShaderLoader.load("assets/shaders/gui.nks")

        while (!window.isCloseRequested()) {
            onRenderFrame()

            if (timer.elapsed()) {
                onTimerTick()
                timer.reset()
            }

            window.update()
        }

        logger.info { "Shutting down..." }
        onShutdown()
        window.destroy()
    }

    open fun onPreInit() = Unit
    open fun onPostInit() = Unit
    open fun onRenderFrame() = Unit
    open fun onTimerTick() = Unit
    open fun onShutdown() = Unit

}