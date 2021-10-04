package de.twometer.neko.render

import de.twometer.neko.core.NekoApp
import de.twometer.neko.util.MathExtensions.clone
import org.joml.Vector2f

enum class ShaderInject(val inject: (Shader) -> Unit) {

    CameraMatrices({
        it["_viewMatrix"] = NekoApp.the.scene.camera.viewMatrix
        it["_projectionMatrix"] = NekoApp.the.scene.camera.projectionMatrix
    }),

    CameraMatricesInverse({
        it["_viewMatrixInverse"] = NekoApp.the.scene.camera.viewMatrix.clone().invert()
        it["_projectionMatrixInverse"] = NekoApp.the.scene.camera.projectionMatrix.clone().invert()
    }),

    CameraPosition({
        it["_cameraPosition"] = NekoApp.the.scene.camera.position
    }),

    ScreenSize({
        val (w, h) = NekoApp.the.window.getSize()
        it["_screenSize"] = Vector2f(w.toFloat(), h.toFloat())
    })

}