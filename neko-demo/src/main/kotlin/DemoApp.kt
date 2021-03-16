import de.twometer.neko.core.AppConfig
import de.twometer.neko.core.NekoApp
import de.twometer.neko.res.AssetManager
import de.twometer.neko.res.ModelLoader
import de.twometer.neko.scene.Color
import de.twometer.neko.scene.PointLight
import de.twometer.neko.util.MathF.toRadians

class DemoApp : NekoApp(AppConfig(windowTitle = "Neko Engine Demo", debugMode = false)) {

    override fun onPreInit() {
        AssetManager.registerPath("./neko-engine/assets")
        AssetManager.registerPath("./neko-demo/assets")
    }

    override fun onPostInit() {
        scene.rootNode.attachChild(ModelLoader.loadFromFile("rin.fbx").also {
            it.transform.rotation.rotateX(toRadians(-90f)).rotateZ(-1.3f)
            it.transform.translation.set(0f, 1f, 0f)
        })

        scene.rootNode.attachChild(ModelLoader.loadFromFile("animegirl.fbx").also {
            it.transform.rotation.rotateX(toRadians(-90f))
            it.transform.translation.set(2f, 0f, 0f)
        })

        scene.rootNode.attachChild(ModelLoader.loadFromFile("test.fbx"))

        scene.rootNode.attachChild(PointLight().also { it.color = Color(1f, 1f, 1f, 1f) })
        scene.rootNode.attachChild(PointLight().also {
            it.color = Color(0f, 1f, 1f, 1f)
            it.transform.translation.set(2f, 2f, 0f)
        })
    }

}

fun main() {
    DemoApp().run()
}