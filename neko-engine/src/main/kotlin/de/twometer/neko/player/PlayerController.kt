package de.twometer.neko.player

import de.twometer.neko.core.Window
import de.twometer.neko.scene.Scene

interface PlayerController {

    fun updateCamera(window: Window, scene: Scene, deltaTime: Double)

}