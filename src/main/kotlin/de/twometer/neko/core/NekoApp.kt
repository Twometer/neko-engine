package de.twometer.neko.core

import de.twometer.neko.Neko
import mu.KotlinLogging
import org.lwjgl.opengl.GL11.*

private val logger = KotlinLogging.logger {}

open class NekoApp {

    private val window = Window(1024, 768, "Neko Engine")

    fun start() {
        window.create()

        val version = glGetString(GL_VERSION)
        val vendor = glGetString(GL_VENDOR)
        println("Starting Neko Engine v${Neko.VERSION}")
        println("Detected OpenGL $version ($vendor)")

        while (!window.closeRequested()) {


            window.update()
        }
        println("Shutting down...")
    }

}