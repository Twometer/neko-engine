package de.twometer.neko.core

import de.twometer.neko.Neko
import de.twometer.neko.audio.OpenAL
import de.twometer.neko.events.Events
import de.twometer.neko.events.ResizeEvent
import de.twometer.neko.events.TickEvent
import de.twometer.neko.gui.GuiManager
import de.twometer.neko.gui.ImGuiHandler
import de.twometer.neko.gui.Page
import de.twometer.neko.player.DefaultPlayerController
import de.twometer.neko.player.PickEngine
import de.twometer.neko.player.PlayerController
import de.twometer.neko.render.FboManager
import de.twometer.neko.render.SceneRenderer
import de.twometer.neko.scene.Scene
import de.twometer.neko.util.CrashHandler
import de.twometer.neko.util.FpsLimiter
import de.twometer.neko.util.Profiler
import de.twometer.neko.util.Timer
import imgui.ImGui
import imgui.flag.ImGuiWindowFlags
import mu.KotlinLogging
import org.lwjgl.opengl.GL11.*

private val logger = KotlinLogging.logger {}

open class NekoApp(config: AppConfig = AppConfig()) {

    companion object {
        private var instance: NekoApp? = null

        val the: NekoApp get() = instance!!
    }

    val window = Window(config)
    val timer = Timer(config.timerSpeed)
    val scene = Scene()
    val renderer = SceneRenderer(scene)
    val pickEngine = PickEngine(scene)
    var playerController: PlayerController = DefaultPlayerController()
    var guiManager: GuiManager = GuiManager()
    var cursorVisible = false
    var fpsLimit = 60
    var loadingPage = "base/loading.html"

    private val performanceProfile = HashMap<String, Double>()
    private val performanceHistory = FloatArray(144)

    fun run() {
        if (instance != null)
            error("Only one NekoApp instance is allowed")
        else instance = this

        logger.info { "Starting Neko Engine v${Neko.VERSION}" }
        CrashHandler.register()
        Events.setup()
        Events.register(this)
        onPreInit()
        window.create()
        logGlInfo()
        OpenAL.open()

        FboManager.setup()
        renderer.setup()
        guiManager.setup()
        ImGuiHandler.setup(window)

        // Initial resize event
        val (width, height) = window.getSize()
        Events.post(ResizeEvent(width, height))

        // Rendering context ready - display the loading screen
        guiManager.page = Page(loadingPage)
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
            Profiler.beginFrame()
            timer.onFrame()
            if (!guiManager.isInputBlocked() && !ImGuiHandler.wantsControl()) {
                playerController.updateCamera(window, scene, timer.deltaTime)
            }
            window.setCursorVisible(guiManager.isInputBlocked() || cursorVisible || ImGuiHandler.wantsControl())
            ImGuiHandler.newFrame()

            scene.camera.update()
            OpenAL.updateListener(scene.camera)
            renderer.renderFrame()
            onRenderFrame()
            guiManager.render()
            ImGuiHandler.render()

            if (timer.elapsed) {
                onTimerTick()
                Events.post(TickEvent())
                timer.reset()
            }

            onPostFrame()
            window.update()
            Profiler.endFrame()
            storeProfilerResults()
            FpsLimiter.sync(fpsLimit)
        }

        logger.info { "Shutting down..." }
        onShutdown()
        ImGuiHandler.shutdown()
        window.destroy()
        OpenAL.close()
    }

    protected fun showDebugWindow() {
        val workspacePos = ImGui.getMainViewport().workPos
        val windowFlags =
            ImGuiWindowFlags.NoDecoration or ImGuiWindowFlags.AlwaysAutoResize or ImGuiWindowFlags.NoSavedSettings or ImGuiWindowFlags.NoNav or ImGuiWindowFlags.NoFocusOnAppearing or ImGuiWindowFlags.NoMove
        ImGui.setNextWindowPos(workspacePos.x + 10f, workspacePos.y + 10f)
        ImGui.setNextWindowBgAlpha(0.55f)
        if (ImGui.begin("Debug", windowFlags)) {
            ImGui.text("FPS: " + timer.fps)
            ImGui.separator()
            ImGui.text("X:" + scene.camera.position.x)
            ImGui.text("Y:" + scene.camera.position.y)
            ImGui.text("Z:" + scene.camera.position.z)
            ImGui.text("LookAt: " + (pickEngine.pick()?.name ?: "nothing"))
            if (Profiler.enabled && performanceProfile.isNotEmpty()) {
                ImGui.separator()
                ImGui.plotLines("frame times", performanceHistory, performanceHistory.size)
                ImGui.separator()
                if (ImGui.beginTable("performance_profile", 2)) {
                    performanceProfile.entries
                        .sortedByDescending { it.value }
                        .forEach {
                            ImGui.tableNextColumn()
                            ImGui.text(it.key)
                            ImGui.tableNextColumn()
                            ImGui.text("${it.value}ms")
                        }
                    ImGui.endTable()
                }
            }
        }
        ImGui.end()
    }

    private fun storeProfilerResults() {
        if (!Profiler.enabled)
            return

        pushPerformanceHistory(performanceProfile["Full frame"]?.toFloat() ?: 0f)
        Profiler.getSections().forEach {
            performanceProfile[it.key] = it.value.duration
        }
    }

    private fun logGlInfo() {
        val version = glGetString(GL_VERSION)
        val vendor = glGetString(GL_VENDOR)
        val os = System.getProperty("os.name")
        logger.info { "Detected OpenGL $version ($vendor) on $os" }
    }

    private fun pushPerformanceHistory(value: Float) {
        for (i in 1 until performanceHistory.size) performanceHistory[i - 1] = performanceHistory[i]
        performanceHistory[performanceHistory.size - 1] = value
    }

    open fun onPreInit() = Unit
    open fun onPostInit() = Unit
    open fun onRenderFrame() = Unit
    open fun onPostFrame() = Unit
    open fun onTimerTick() = Unit
    open fun onShutdown() = Unit

}