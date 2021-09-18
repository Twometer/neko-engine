package de.twometer.neko.render

import de.twometer.neko.core.NekoApp
import de.twometer.neko.util.MathExtensions.clone
import org.joml.Vector2f

enum class ShaderInject(val inject: (Shader) -> Unit) {

    CameraMatrices({
        it["viewMatrix"] = NekoApp.the!!.scene.camera.viewMatrix
        it["projectionMatrix"] = NekoApp.the!!.scene.camera.projectionMatrix
    }),

    CameraMatricesInverse({
        it["projectionMatrixInverse"] = NekoApp.the!!.scene.camera.projectionMatrix.clone().invert()
    }),

    CameraPosition({
        it["cameraPos"] = NekoApp.the!!.scene.camera.position
    }),

    ScreenSize({
        val (w, h) = NekoApp.the!!.window.getSize()
        it["screenSize"] = Vector2f(w.toFloat(), h.toFloat())
    })

}