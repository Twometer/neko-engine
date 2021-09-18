import de.twometer.neko.core.AppConfig
import de.twometer.neko.core.NekoApp
import de.twometer.neko.events.KeyPressEvent
import de.twometer.neko.res.AssetManager
import de.twometer.neko.res.CubemapCache
import de.twometer.neko.res.ModelLoader
import de.twometer.neko.scene.Color
import de.twometer.neko.scene.nodes.ModelNode
import de.twometer.neko.scene.nodes.Node
import de.twometer.neko.scene.nodes.PointLight
import de.twometer.neko.scene.nodes.Sky
import de.twometer.neko.util.MathF.toRadians
import imgui.ImGui
import org.greenrobot.eventbus.Subscribe
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE
import org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT

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

        scene.rootNode.attachChild(ModelLoader.load("ground.fbx").also {
            it.transform.rotation.rotateX(toRadians(-90f))
            it.transform.translation.y = -1f
        })

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

    var selectedNode: Node? = null

    override fun onRenderFrame() {
        ImGui.begin("Scenegraph")
        fun drawNode(node: Node) {
            ImGui.pushID(node.id)
            val open = ImGui.treeNode("${node.id} - ${node.javaClass.simpleName} [${node.name}]")
            if (ImGui.isItemClicked()) {
                selectedNode = node
            }
            if (open) {
                node.children.forEach(::drawNode)
                ImGui.treePop()
            }
            ImGui.popID()
        }
        drawNode(scene.rootNode)
        ImGui.end()

        ImGui.begin("Debug")
        ImGui.text("FPS: " + timer.fps)
        ImGui.end()

        selectedNode?.apply {
            ImGui.begin("Node info")
            ImGui.text("POS:" + this.transform.translation)
            ImGui.text("ROT:" + this.transform.rotation)
            ImGui.text("SCALE:" + this.transform.scale)
            ImGui.end()
        }
    }

    @Subscribe
    fun onKeyPress(e: KeyPressEvent) {
        if (e.key == GLFW_KEY_ESCAPE) {
            if (guiManager.page == null)
                guiManager.page = DemoPausePage()
            else
                guiManager.page = null
        }
        if (e.key == GLFW_KEY_LEFT_ALT) {
            this.cursorVisible = !this.cursorVisible
        }
    }
}

fun main() {
    DemoApp().run()
}