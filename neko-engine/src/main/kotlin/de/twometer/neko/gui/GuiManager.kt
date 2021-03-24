package de.twometer.neko.gui

import com.labymedia.ultralight.UltralightRenderer
import com.labymedia.ultralight.UltralightView
import com.labymedia.ultralight.plugin.loading.UltralightLoadListener
import de.twometer.neko.core.NekoApp
import de.twometer.neko.events.Events
import de.twometer.neko.events.ResizeEvent
import mu.KotlinLogging
import org.greenrobot.eventbus.Subscribe

private val logger = KotlinLogging.logger {}

class GuiManager : UltralightLoadListener {

    private lateinit var renderer: UltralightRenderer
    private lateinit var view: UltralightView

    fun setup() {
        Events.register(this)
        UltralightLoader.load()
        renderer = UltralightRenderer.create()

        val (width, height) = NekoApp.the!!.window.getSize()
        view = renderer.createView(width.toLong(), height.toLong(), true)
        view.setViewListener(UltralightNekoViewListener())

    }

    fun render() {

    }

    @Subscribe
    fun onResize(event: ResizeEvent) = view.resize(event.width.toLong(), event.height.toLong())

    override fun onDOMReady(frameId: Long, isMainFrame: Boolean, url: String?) {

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

    override fun onBeginLoading(frameId: Long, isMainFrame: Boolean, url: String?) = Unit
    override fun onFinishLoading(frameId: Long, isMainFrame: Boolean, url: String?) = Unit
    override fun onUpdateHistory() = Unit
    override fun onWindowObjectReady(frameId: Long, isMainFrame: Boolean, url: String?) = Unit

}