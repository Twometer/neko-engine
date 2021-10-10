package de.twometer.neko.gui

import com.google.gson.Gson
import de.twometer.neko.core.NekoApp

open class Page(val path: String) {

    private val gson = Gson()
    private val guiManager = NekoApp.the.guiManager

    open fun onDocumentReady() = Unit

    open fun onLoaded() = Unit

    open fun onUnloaded() = Unit

    open fun blocksGameInput() = false

    open fun close() {
        if (guiManager.page == this)
            guiManager.page = null
    }

    protected fun runScript(script: String): String = guiManager.runScript(script)

    protected fun setProperty(property: String, value: Any) {
        runScript("$property = ${gson.toJson(value)};")
    }

    protected fun setElementProperty(elementId: String, property: String, value: String) {
        runScript("document.getElementById('$elementId')['$property'] = '${escapeJs(value)}';")
    }

    protected fun setElementText(elementId: String, text: String) {
        runScript("document.getElementById('$elementId').innerText = '${escapeJs(text)}';")
    }

    protected fun call(function: String, vararg params: Any): String {
        val serializedParams = params.joinToString(", ") { gson.toJson(it) }
        return runScript("$function($serializedParams);")
    }

    protected fun <T> String.parseJson(clazz: Class<T>): T = gson.fromJson(this, clazz)

    private fun escapeJs(str: String): String =
        str.trimEnd('\\')
            .replace("'", "\\'")

}