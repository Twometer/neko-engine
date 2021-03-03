import de.twometer.neko.core.AppConfig
import de.twometer.neko.core.NekoApp
import de.twometer.neko.res.AssetManager
import de.twometer.neko.res.ModelLoader

class DemoApp : NekoApp(AppConfig(windowTitle = "Neko Engine Demo", debugMode = true)) {

    override fun onPreInit() {
        AssetManager.registerPath("./neko-engine/assets")
        AssetManager.registerPath("./neko-demo/assets")
    }

    override fun onPostInit() {
        val model = ModelLoader.loadFromFile("demo/models/animegirl.fbx")
        scene.rootNode.attachChild(model)
    }

}

fun main() {
    DemoApp().run()
}