package de.twometer.neko.core

import de.twometer.neko.Neko
import de.twometer.neko.events.Events
import de.twometer.neko.res.ShaderCache
import de.twometer.neko.res.TextureCache
import de.twometer.neko.scene.Geometry
import de.twometer.neko.scene.MatKey
import de.twometer.neko.scene.Scene
import de.twometer.neko.util.CrashHandler
import de.twometer.neko.util.MathExtensions.clone
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

    fun run() {
        if (the != null)
            error("Only one NekoApp instance is allowed")
        else the = this

        logger.info { "Starting Neko Engine v${Neko.VERSION}" }
        CrashHandler.register()
        Events.setup()

        onPreInit()

        window.create()

        val version = glGetString(GL_VERSION)
        val vendor = glGetString(GL_VENDOR)
        val os = System.getProperty("os.name")
        logger.info { "Detected OpenGL $version ($vendor) on $os" }

        if (config.debugMode) {
            GLUtil.setupDebugMessageCallback()
            logger.info { "Enabled debug messages" }
        }

        glEnable(GL_DEPTH_TEST)
        //glEnable(GL_CULL_FACE)
        //glCullFace(GL_BACK)
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        onPostInit()

        while (!window.isCloseRequested()) {
            scene.camera.update()

            renderScene()
            onRenderFrame()

            if (timer.elapsed()) {
                onTimerTick()
                handleControls()
                timer.reset()
            }

            window.update()
        }

        logger.info { "Shutting down..." }
        onShutdown()
        window.destroy()
    }

    private fun renderScene() {
        val (width, height) = window.getSize()
        glViewport(0, 0, width, height)
        glClearColor(scene.backgroundColor.r, scene.backgroundColor.g, scene.backgroundColor.b, scene.backgroundColor.a)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        scene.rootNode.scanTree { node ->
            if (node is Geometry) {
                val shader = ShaderCache.get(node.material.shader)
                val tex = node.material[MatKey.TextureDiffuse]
                tex?.also { TextureCache.get(it.toString()).bind() }

                shader.bind()
                shader["viewMatrix"] = scene.camera.viewMatrix
                shader["projectionMatrix"] = scene.camera.projectionMatrix
                shader["modelMatrix"] = node.compositeTransform.matrix

                node.render()
            }
        }
    }

    private fun handleControls() {
        val speed = 0.15f
        val sensitivity = 0.002f

        if (window.isKeyDown(GLFW_KEY_W)) {
            scene.camera.position.add(scene.camera.direction.clone().mul(speed))
        }
        if (window.isKeyDown(GLFW_KEY_S)) {
            scene.camera.position.sub(scene.camera.direction.clone().mul(speed))
        }
        if (window.isKeyDown(GLFW_KEY_A)) {
            scene.camera.position.sub(scene.camera.right.clone().mul(speed))
        }
        if (window.isKeyDown(GLFW_KEY_D)) {
            scene.camera.position.add(scene.camera.right.clone().mul(speed))
        }
        if (window.isKeyDown(GLFW_KEY_SPACE)) {
            scene.camera.position.y += speed
        }
        if (window.isKeyDown(GLFW_KEY_LEFT_SHIFT)) {
            scene.camera.position.y -= speed
        }

        val pos = window.getCursorPosition()

        val dx = 10 - pos.first
        val dy = 10 - pos.second

        scene.camera.rotation.x += dx.toFloat() * sensitivity
        scene.camera.rotation.y += dy.toFloat() * sensitivity

        window.setCursorPosition(10, 10)
    }

    open fun onPreInit() = Unit
    open fun onPostInit() = Unit
    open fun onRenderFrame() = Unit
    open fun onTimerTick() = Unit
    open fun onShutdown() = Unit

}