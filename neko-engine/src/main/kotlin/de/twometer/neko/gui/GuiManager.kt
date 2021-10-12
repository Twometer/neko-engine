package de.twometer.neko.gui

import com.labymedia.ultralight.UltralightRenderer
import com.labymedia.ultralight.UltralightView
import com.labymedia.ultralight.bitmap.UltralightBitmapSurface
import com.labymedia.ultralight.databind.Databind
import com.labymedia.ultralight.databind.DatabindConfiguration
import com.labymedia.ultralight.javascript.JavascriptEvaluationException
import com.labymedia.ultralight.plugin.loading.UltralightLoadListener
import de.twometer.neko.core.NekoApp
import de.twometer.neko.events.Events
import de.twometer.neko.events.ResizeEvent
import de.twometer.neko.render.OpenGL
import de.twometer.neko.render.Primitives
import de.twometer.neko.render.Shader
import de.twometer.neko.render.Texture2d
import de.twometer.neko.res.ShaderCache
import mu.KotlinLogging
import org.greenrobot.eventbus.Subscribe
import org.lwjgl.opengl.GL12.*
import java.nio.ByteBuffer

private val logger = KotlinLogging.logger {}

class GuiManager : UltralightLoadListener {

    private val globalObjects = HashMap<String, Any>()
    private lateinit var shader: Shader
    private lateinit var renderer: UltralightRenderer
    private lateinit var view: UltralightView
    private lateinit var contextProvider: PageContextProvider

    private val loadLock = Object()
    var finishedLoading: Boolean = false
        private set

    var page: Page? = null
        set(newVal) {
            synchronized(loadLock) {
                if (!finishedLoading && field != null)
                    loadLock.wait()
            }

            field?.onUnloaded()
            field = newVal
            finishedLoading = false
            if (newVal != null)
                view.loadURL("file:///${newVal.path}")
        }

    private val texture: Texture2d by lazy {
        val id = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, id)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glBindTexture(GL_TEXTURE_2D, 0)
        Texture2d(id, 0, 0)
    }

    fun setup() {
        Events.register(this)
        UltralightLoader.load()
        renderer = UltralightRenderer.create()

        val (width, height) = NekoApp.the.window.getSize()
        view = renderer.createView(width.toLong(), height.toLong(), true)
        view.setViewListener(UltralightNekoViewListener())
        view.setLoadListener(this)
        view.focus()
        Events.register(UltralightNekoInputAdapter(view))

        contextProvider = PageContextProvider(view)
        contextProvider.syncWithJavascript {
            val config = DatabindConfiguration.builder().contextProviderFactory(contextProvider).build()
            contextProvider.databind = Databind(config)
        }

        shader = ShaderCache.get("base/gui.nks")

        logger.info { "GUI system loaded" }
    }

    fun render() {
        if (page == null)
            return

        OpenGL.disable(GL_DEPTH_TEST)
        shader.bind()
        texture.bind()

        renderer.update()
        renderer.render()

        val surface = view.surface() as UltralightBitmapSurface
        val bitmap = surface.bitmap()
        val dirtyBounds = surface.dirtyBounds()
        val (width, height) = view.width().toInt() to view.height().toInt()

        if (dirtyBounds.isValid) {
            val imageData: ByteBuffer = bitmap.lockPixels()
            glPixelStorei(GL_UNPACK_ROW_LENGTH, bitmap.rowBytes().toInt() / 4)
            if (dirtyBounds.width() == width && dirtyBounds.height() == height) {
                // Update full image
                glTexImage2D(
                    GL_TEXTURE_2D,
                    0,
                    GL_RGBA8,
                    width,
                    height,
                    0,
                    GL_BGRA,
                    GL_UNSIGNED_INT_8_8_8_8_REV,
                    imageData
                )
                glPixelStorei(GL_UNPACK_ROW_LENGTH, 0)
            } else {
                // Update partial image
                val x = dirtyBounds.x()
                val y = dirtyBounds.y()
                val dirtyWidth = dirtyBounds.width()
                val dirtyHeight = dirtyBounds.height()
                val startOffset = (y * bitmap.rowBytes() + x * 4).toInt()
                glTexSubImage2D(
                    GL_TEXTURE_2D,
                    0,
                    x, y, dirtyWidth, dirtyHeight,
                    GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV,
                    imageData.position(startOffset)
                )
            }
            glPixelStorei(GL_UNPACK_ROW_LENGTH, 0)
            bitmap.unlockPixels()
            surface.clearDirtyBounds()
        }

        Primitives.unitQuad.render()

        shader.unbind()
        OpenGL.enable(GL_DEPTH_TEST)
    }

    fun runScript(script: String): String {
        return try {
            view.evaluateScript(script)
        } catch (e: JavascriptEvaluationException) {
            logger.error(e) { "Failed to run JavaScript" }
            "undefined"
        }
    }

    fun registerGlobalObject(key: String, value: Any) {
        globalObjects[key] = value
    }

    @Subscribe
    fun onResize(event: ResizeEvent) = view.resize(event.width.toLong(), event.height.toLong())

    fun isInputBlocked() = page?.blocksGameInput() ?: false

    override fun onDOMReady(frameId: Long, isMainFrame: Boolean, url: String?) {
        page?.onDocumentReady()
    }

    override fun onFailLoading(
        frameId: Long,
        isMainFrame: Boolean,
        url: String?,
        description: String?,
        errorDomain: String?,
        errorCode: Int
    ) {
        logger.error { "Ultralight failed loading '$url': $description ($errorCode @ $errorDomain)" }
    }

    override fun onFinishLoading(frameId: Long, isMainFrame: Boolean, url: String?) {
        page?.run {
            contextProvider.syncWithJavascript {
                contextProvider.registerObject(it.context, "_remote", this)
                for (global in globalObjects)
                    contextProvider.registerObject(it.context, global.key, global.value)
                this.onLoaded()
                runScript("if (typeof OnLoad !== 'undefined') { OnLoad(); }")

                synchronized(loadLock) {
                    finishedLoading = true
                    loadLock.notifyAll()
                }
            }
        }
    }

    override fun onBeginLoading(frameId: Long, isMainFrame: Boolean, url: String?) {
        synchronized(loadLock) {
            finishedLoading = false
        }
    }

    override fun onUpdateHistory() = Unit
    override fun onWindowObjectReady(frameId: Long, isMainFrame: Boolean, url: String?) = Unit

}