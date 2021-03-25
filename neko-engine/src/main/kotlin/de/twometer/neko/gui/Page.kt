package de.twometer.neko.gui

import de.twometer.neko.core.NekoApp

open class Page(val path: String) {

    private val guiManager = NekoApp.the!!.guiManager

    open fun onDocumentReady() = Unit

    open fun onLoaded() = Unit

    open fun onUnloaded() = Unit

    open fun blocksGameInput() = false

    fun close() {
        if (guiManager.page == this)
            guiManager.page = null
    }

    protected fun runScript(script: String) = guiManager.runScript(script)

}