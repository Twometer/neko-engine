import de.twometer.neko.core.AppConfig
import de.twometer.neko.core.NekoApp
import de.twometer.neko.events.KeyPressEvent
import de.twometer.neko.res.AssetManager
import de.twometer.neko.res.CubemapCache
import de.twometer.neko.res.ModelLoader
import de.twometer.neko.scene.Color
import de.twometer.neko.scene.nodes.ModelNode
import de.twometer.neko.scene.nodes.PointLight
import de.twometer.neko.scene.nodes.Sky
import de.twometer.neko.util.MathF.toRadians
import org.greenrobot.eventbus.Subscribe
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE

class DemoApp : NekoApp(AppConfig(windowTitle = "Neko Engine Demo")) {

    private lateinit var rin: ModelNode
    private lateinit var sky: Sky

    override fun onPreInit() {
        AssetManager.registerPath("./neko-engine/assets")
        AssetManager.registerPath("./neko-demo/assets")
    }

    override fun onPostInit() {
        scene.rootNode.attachChild(ModelLoader.load("rin.fbx").also {
            it.transform.translation.set(0.75f, 0f, 0f)
            it.transform.scale.set(0.01, 0.01, 0.01)
            it.playAnimation(it.animations[0])
            rin = it
        })

        scene.rootNode.attachChild(ModelLoader.load("girl.fbx").also {
            it.transform.translation.set(2f, 0f, 0f)
            it.transform.scale.set(0.01, 0.01, 0.01)
            it.playAnimation(it.animations[0])
        })

        scene.rootNode.attachChild(ModelLoader.load("test.fbx"))

        scene.rootNode.attachChild(ModelLoader.load("demo-run.fbx").also {
            it.transform.translation.set(5f, 0f, 0f)
            it.transform.rotation.rotateY(toRadians(15f))
            it.transform.scale.set(0.01, 0.01, 0.01)
            it.playAnimation(it.animations[0])
        })

        scene.rootNode.attachChild(PointLight().also { it.color = Color(1f, 1f, 1f, 1f) })
        scene.rootNode.attachChild(PointLight().also {
            it.color = Color(0f, 1f, 1f, 1f)
            it.transform.translation.set(2f, 2f, 0f)
        })

        sky = Sky(CubemapCache.get("skybox"))
        scene.rootNode.attachChild(sky)
    }

    override fun onTimerTick() {
        sky.transform.rotation.rotateY(0.0002f)
        rin.transform.translation.z += 0.025f
    }

    @Subscribe
    fun onKeyPress(e: KeyPressEvent) {
        if (e.key == GLFW_KEY_ESCAPE) {
            if (guiManager.page == null)
                guiManager.page = DemoPausePage()
            else
                guiManager.page = null
        }
    }
}

fun main() {
    DemoApp().run()
}