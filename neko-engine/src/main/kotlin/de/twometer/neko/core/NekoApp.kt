package de.twometer.neko.core

import de.twometer.neko.Neko
import de.twometer.neko.events.Events
import de.twometer.neko.events.ResizeEvent
import de.twometer.neko.events.TickEvent
import de.twometer.neko.gui.GuiManager
import de.twometer.neko.gui.ImGuiHandler
import de.twometer.neko.gui.Page
import de.twometer.neko.player.DefaultPlayerController
import de.twometer.neko.player.PlayerController
import de.twometer.neko.render.FboManager
import de.twometer.neko.render.SceneRenderer
import de.twometer.neko.scene.Scene
import de.twometer.neko.util.CrashHandler
import de.twometer.neko.util.Timer
import mu.KotlinLogging
import org.lwjgl.opengl.GL11.*

private val logger = KotlinLogging.logger {}

open class NekoApp(config: AppConfig = AppConfig()) {

    companion object {
        var the: NekoApp? = null
    }

    val window = Window(config)
    val timer = Timer(config.timerSpeed)
    val scene = Scene()
    val renderer = SceneRenderer(scene)
    var playerController: PlayerController = DefaultPlayerController()
    var guiManager: GuiManager = GuiManager()
    var cursorVisible = false

    fun run() {
        if (the != null)
            error("Only one NekoApp instance is allowed")
        else the = this

        logger.info { "Starting Neko Engine v${Neko.VERSION}" }
        CrashHandler.register()
        Events.setup()
        Events.register(this)
        onPreInit()
        window.create()

        logGlInfo()

        FboManager.setup()
        renderer.setup()
        guiManager.setup()
        ImGuiHandler.setup(window)

        // Initial resize event
        val (width, height) = window.getSize()
        Events.post(ResizeEvent(width, height))

        // Rendering context ready - display the loading screen
        guiManager.page = Page("base/loading.html")
        do {
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
            guiManager.render()
            window.update()
        } while (!guiManager.finishedLoading)
        guiManager.page = null


        // Big chunky user init
        onPostInit()

        logger.info { "Neko Engine initialized" }
        timer.reset()

        while (!window.isCloseRequested()) {
            if (!guiManager.isInputBlocked() && !ImGuiHandler.wantsControl()) {
                playerController.updateCamera(window, scene, timer.deltaTime)
            }
            window.setCursorVisible(guiManager.isInputBlocked() || cursorVisible || ImGuiHandler.wantsControl())
            ImGuiHandler.newFrame()

            scene.camera.update()
            renderer.renderFrame()
            onRenderFrame()
            guiManager.render()
            ImGuiHandler.render()

            if (timer.elapsed) {
                onTimerTick()
                Events.post(TickEvent())
                timer.reset()
            }

            timer.onFrame()
            window.update()
        }

        logger.info { "Shutting down..." }
        onShutdown()
        ImGuiHandler.shutdown()
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