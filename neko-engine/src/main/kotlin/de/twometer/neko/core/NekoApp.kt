package de.twometer.neko.core

import de.twometer.neko.Neko
import de.twometer.neko.events.Events
import de.twometer.neko.events.ResizeEvent
import de.twometer.neko.player.DefaultPlayerController
import de.twometer.neko.player.PlayerController
import de.twometer.neko.render.SceneRenderer
import de.twometer.neko.scene.Scene
import de.twometer.neko.util.CrashHandler
import de.twometer.neko.util.Timer
import mu.KotlinLogging
import org.lwjgl.opengl.GL11.*

private val logger = KotlinLogging.logger {}

open class NekoApp(config: AppConfig) {

    companion object {
        var the: NekoApp? = null
    }

    val window = Window(config)
    val timer = Timer(config.timerSpeed)
    val scene = Scene()
    val renderer = SceneRenderer(scene, window)
    var playerController: PlayerController = DefaultPlayerController()

    fun run() {
        if (the != null)
            error("Only one NekoApp instance is allowed")
        else the = this

        logger.info { "Starting Neko Engine v${Neko.VERSION}" }
        CrashHandler.register()
        Events.setup()
        onPreInit()
        window.create()

        logGlInfo()

        renderer.setup()

        // Initial resize event
        val (width, height) = window.getSize()
        Events.post(ResizeEvent(width, height))
        onPostInit()

        while (!window.isCloseRequested()) {
            scene.camera.update()

            renderer.renderFrame()
            onRenderFrame()

            if (timer.elapsed()) {
                onTimerTick()
                playerController.updateCamera(window, scene)
                timer.reset()
            }

            window.update()
        }

        logger.info { "Shutting down..." }
        onShutdown()
        window.destroy()
    }

    private fun logGlInfo() {
        val version = glGetString(GL_VERSION)
        val vendor = glGetString(GL_VENDOR)
        val os = System.getProperty("os.name")
        logger.info { "Detected OpenGL $version ($vendor) on $os" }
    }

    open fun onPreInit() = Unit
    open fun onPostInit() = Unit
    open fun onRenderFrame() = Unit
    open fun onTimerTick() = Unit
    open fun onShutdown() = Unit

}