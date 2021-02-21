import de.twometer.neko.core.AppConfig
import de.twometer.neko.core.NekoApp
import de.twometer.neko.res.AssetFiles
import de.twometer.neko.res.ModelLoader
import de.twometer.neko.res.ShaderLoader

class DemoApp : NekoApp(AppConfig(windowTitle = "Neko Engine Demo")) {

    override fun onPreInit() {
        AssetFiles.registerPath("./neko-engine/assets")
        AssetFiles.registerPath("./neko-demo/assets")
    }

    override fun onPostInit() {
        ShaderLoader.loadFromFile("base/shaders/gui.nks")
        ModelLoader.loadFromFile("demo/models/animegirl.fbx")
    }
}

fun main() {
    DemoApp().run()
}