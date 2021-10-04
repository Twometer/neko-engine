import de.twometer.neko.core.Window
import de.twometer.neko.player.PlayerController
import de.twometer.neko.scene.Scene

class BenchmarkPlayerController : PlayerController {

    override fun updateCamera(window: Window, scene: Scene, deltaTime: Double) {
        scene.camera.position.set(40f, 0.6f, -6.5f)
        scene.camera.rotation.set(-2.5f, 0f)
    }

}