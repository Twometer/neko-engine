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
    private val performanceResize: Boolean,
    private val config: (Framebuffer) -> Unit
) {

    fun resize(width: Float, height: Float, scale: Float) {
        if (performanceResize) {
            resize(width * scale, height * scale)
        } else {
            resize(width, height)
        }
    }

    private fun resize(width: Float, height: Float) {
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

    private const val scale = 0.5f

    fun setup() {
        Events.register(this)
    }

    @Subscribe
    fun onSizeChanged(event: ResizeEvent) {
        for (handle in framebufferRefs)
            handle.resize(event.width.toFloat(), event.height.toFloat(), scale)
    }

    fun request(configure: (Framebuffer) -> Unit, performanceResize: Boolean = false): FramebufferRef {
        val (width, height) = NekoApp.the!!.window.getSize()
        val framebuffer = Framebuffer(width, height).also(configure)
            .verify()

        val ref = FramebufferRef(framebuffer, performanceResize, configure)
        framebufferRefs.add(ref)
        return ref
    }

}