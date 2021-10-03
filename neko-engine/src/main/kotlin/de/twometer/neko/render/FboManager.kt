package de.twometer.neko.render

import de.twometer.neko.core.NekoApp
import de.twometer.neko.events.Events
import de.twometer.neko.events.ResizeEvent
import org.greenrobot.eventbus.Subscribe

/**
 * Holds a reference to an automatically managed framebuffer
 */
data class FramebufferRef(
    var fbo: Framebuffer,
    private val scale: Float,
    private val config: (Framebuffer) -> Unit
) {

    fun resize(width: Float, height: Float) {
        resizeImpl(width * scale, height * scale)
    }

    private fun resizeImpl(width: Float, height: Float) {
        fbo.destroy()
        fbo = Framebuffer(width.toInt(), height.toInt()).also(config)
            .verify()
    }

    fun bind() = fbo.bind()

    fun unbind() = fbo.unbind()

}

/**
 * Manages and resizes frame buffer objects automatically
 */
object FboManager {

    private val framebufferRefs = ArrayList<FramebufferRef>()

    fun setup() {
        Events.register(this)
    }

    @Subscribe
    fun onSizeChanged(event: ResizeEvent) {
        for (handle in framebufferRefs)
            handle.resize(event.width.toFloat(), event.height.toFloat())
    }

    fun request(configure: (Framebuffer) -> Unit, scale: Float = 1.0f): FramebufferRef {
        val (width, height) = NekoApp.the.window.getSize()
        val framebuffer = Framebuffer((width * scale).toInt(), (height * scale).toInt())
            .also(configure)
            .verify()

        val ref = FramebufferRef(framebuffer, scale, configure)
        framebufferRefs.add(ref)
        return ref
    }

}