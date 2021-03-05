import de.twometer.neko.core.AppConfig
import de.twometer.neko.core.NekoApp
import de.twometer.neko.res.AssetManager
import de.twometer.neko.res.ModelLoader

class DemoApp : NekoApp(AppConfig(windowTitle = "Neko Engine Demo", debugMode = false)) {

    override fun onPreInit() {
        AssetManager.registerPath("./neko-engine/assets")
        AssetManager.registerPath("./neko-demo/assets")
    }

    override fun onPostInit() {
        val model1 = ModelLoader.loadFromFile("rin.fbx")
        val model2 = ModelLoader.loadFromFile("animegirl.fbx")
        scene.rootNode.attachChild(model1)
    }

}

fun main() {
    DemoApp().run()
}