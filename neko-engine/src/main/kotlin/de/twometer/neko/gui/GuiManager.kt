package de.twometer.neko.gui

import com.labymedia.ultralight.UltralightRenderer
import com.labymedia.ultralight.UltralightView
import de.twometer.neko.core.NekoApp

class GuiManager {

    private lateinit var renderer: UltralightRenderer
    private lateinit var view: UltralightView

    fun setup() {
        UltralightLoader.load()
        renderer = UltralightRenderer.create()

        val (width, height) = NekoApp.the!!.window.getSize()
        view = renderer.createView(width.toLong(), height.toLong(), true)
        view.setViewListener(UltralightNekoViewListener())

    }

}