package de.twometer.neko.core

import de.twometer.neko.Neko
import de.twometer.neko.events.Events
import de.twometer.neko.player.DefaultPlayerController
import de.twometer.neko.player.PlayerController
import de.twometer.neko.render.SceneRenderer
import de.twometer.neko.scene.Scene
import de.twometer.neko.util.CrashHandler
import de.twometer.neko.util.Timer
import mu.KotlinLogging
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GLUtil

private val logger = KotlinLogging.logger {}

open class NekoApp(private val config: AppConfig) {

    companion object {
        var the: NekoApp? = null
    }

    val window = Window(config)
    val timer = Timer(config.timerSpeed)
    val scene = Scene()
    val renderer = SceneRenderer(scene)
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
        if (config.debugMode) {
            GLUtil.setupDebugMessageCallback()
            logger.info { "Enabled debug messages" }
        }

        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)
        glCullFace(GL_BACK)
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        onPostInit()

        while (!window.isCloseRequested()) {
            scene.camera.update()

            renderFrame()
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

    private fun renderFrame() {
        val (width, height) = window.getSize()
        glViewport(0, 0, width, height)
        glClearColor(scene.backgroundColor.r, scene.backgroundColor.g, scene.backgroundColor.b, scene.backgroundColor.a)
        renderer.renderFrame()
    }

    open fun onPreInit() = Unit
    open fun onPostInit() = Unit
    open fun onRenderFrame() = Unit
    open fun onTimerTick() = Unit
    open fun onShutdown() = Unit

}