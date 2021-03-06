package de.twometer.neko.render

import org.joml.Vector2f
import de.twometer.neko.core.NekoApp

enum class ShaderInject(val inject: (Shader) -> Unit) {

    CameraMatrices({
        it["viewMatrix"] = NekoApp.the!!.scene.camera.viewMatrix
        it["projectionMatrix"] = NekoApp.the!!.scene.camera.projectionMatrix
    }),

    CameraPosition({
        it["cameraPos"] = NekoApp.the!!.scene.camera.position
    }),

    ScreenSize({
        val (w, h) = NekoApp.the!!.window.getSize()
        it["screenSize"] = Vector2f(w.toFloat(), h.toFloat())
    })

}