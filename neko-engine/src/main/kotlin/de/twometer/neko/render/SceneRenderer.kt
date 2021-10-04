package de.twometer.neko.render

import de.twometer.neko.core.NekoApp
import de.twometer.neko.events.Events
import de.twometer.neko.events.RenderDeferredEvent
import de.twometer.neko.events.RenderForwardEvent
import de.twometer.neko.events.ResizeEvent
import de.twometer.neko.res.ShaderCache
import de.twometer.neko.res.TextureCache
import de.twometer.neko.scene.Color
import de.twometer.neko.scene.MatKey
import de.twometer.neko.scene.RenderBucket
import de.twometer.neko.scene.Scene
import de.twometer.neko.scene.nodes.PointLight
import de.twometer.neko.scene.nodes.RenderableNode
import de.twometer.neko.util.Profiler
import org.greenrobot.eventbus.Subscribe
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Vector4f
import org.lwjgl.opengl.GL30.*

class SceneRenderer(val scene: Scene) {

    // For more info on these variables, see lighting.blinn.nks
    private val maxLights = 512
    private val lightSize = 112

    lateinit var effectsPipeline: EffectsPipeline
    private lateinit var gBuffer: FramebufferRef
    private lateinit var sceneBuffer: FramebufferRef
    private lateinit var blinnShader: Shader
    private lateinit var blinnBuffer: UniformBuffer
    private lateinit var ambientShader: Shader

    var lightRadius = 35

    private var numActiveLights = 0

    fun setup() {
        Events.register(this)

        // Shaders
        blinnShader = ShaderCache.get("base/lighting.blinn.nks")
        ambientShader = ShaderCache.get("base/lighting.ambient.nks")

        // Framebuffers
        gBuffer = FboManager.request({
            it.addDepthTexture()
                .addColorTexture(0, GL_RGBA16F, GL_RGBA, GL_LINEAR, GL_FLOAT)  // NormalSpecular
                .addColorTexture(1, GL_RGBA16F, GL_RGBA, GL_LINEAR, GL_FLOAT)  // AlbedoShininess
                .addColorTexture(2, GL_RGB16F, GL_RGB, GL_LINEAR, GL_FLOAT)    // Emissive
        })

        sceneBuffer = FboManager.request({
            it.addDepthBuffer()
                .addColorTexture(0, GL_RGBA16F, GL_RGBA, GL_LINEAR, GL_FLOAT)
        })

        effectsPipeline = EffectsPipeline(gBuffer, sceneBuffer)
        blinnBuffer = UniformBuffer(maxLights * lightSize)
        blinnShader.bindUniformBuffer("LightsBlock", blinnBuffer, 0)
    }

    @Subscribe
    fun onSizeChanged(event: ResizeEvent) {
        glViewport(0, 0, event.width, event.height)
    }

    private fun shouldRenderLight(light: PointLight): Boolean {
        val position = light.compositeTransform.translation
        return light.active
                && scene.camera.isInFrustum(position, light.radius)
                && scene.camera.position.distanceSquared(position) < lightRadius * lightRadius
    }

    private fun gatherLights(): List<PointLight> {
        val lights = ArrayList<PointLight>()
        scene.rootNode.scanTree {
            if (it is PointLight && shouldRenderLight(it)) {
                lights.add(it)
            }
        }
        return lights
    }

    private fun updateLights() {
        val lights = gatherLights()
        numActiveLights = lights.size

        val prevHash = blinnBuffer.hash()
        blinnBuffer.rewind()
        for (light in lights) {
            val transform = light.compositeTransform
            blinnBuffer.writeMat4(transform.matrix.scale(light.radius))
            blinnBuffer.writeVec4(Vector4f(light.color.r, light.color.g, light.color.b, light.color.a))
            blinnBuffer.writeVec4(Vector4f(transform.translation, 0f)) // 0f is padding
            blinnBuffer.writeFloat(light.constant)
            blinnBuffer.writeFloat(light.linear)
            blinnBuffer.writeFloat(light.quadratic)
            blinnBuffer.writeFloat(0f) // again, padding
        }

        if (blinnBuffer.hash() == prevHash)
            return

        Profiler.begin("Light upload")
        blinnBuffer.bind()
        blinnBuffer.upload()
        blinnBuffer.unbind()
        Profiler.end()
    }

    fun renderFrame() {
        glClearColor(0f, 0f, 0f, 1f)

        // Render scene to GBuffer
        renderGBuffer()

        // Transfer depth to scene buffer
        Profiler.begin("Depth Blit")
        bindGBuffer()
        gBuffer.fbo.blit(GL_DEPTH_BUFFER_BIT, sceneBuffer.fbo)
        Profiler.end()

        // Setup scene buffer for main draw
        Profiler.begin("Scene buffer setup")
        sceneBuffer.bind()
        glClear(GL_COLOR_BUFFER_BIT)

        OpenGL.enable(GL_BLEND)
        glBlendFunc(GL_ONE, GL_ONE)
        Profiler.end()

        // Ambient lighting pass
        Profiler.begin("Ambient lighting")
        OpenGL.disable(GL_DEPTH_TEST)
        OpenGL.depthMask(false)
        ambientShader.bind()
        ambientShader["ambientStrength"] = scene.ambientStrength
        ambientShader["backgroundColor"] = scene.backgroundColor
        Primitives.unitQuad.render()
        Profiler.end()

        // Blinn-Phong pass (point lights)
        updateLights()
        Profiler.begin("Deferred light pass")
        if (numActiveLights > 0) {
            OpenGL.enable(GL_CULL_FACE)
            OpenGL.cullFace(GL_FRONT)
            OpenGL.enable(GL_DEPTH_TEST)
            OpenGL.depthFunc(GL_GEQUAL)

            blinnShader.bind()
            Primitives.unitSphere.renderInstanced(numActiveLights)
        }
        Profiler.end()

        // Restore GL state
        OpenGL.resetState()
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        // Forward rendering pass
        Profiler.begin("Forward pass")
        scene.rootNode.scanTree { node ->
            if (node is RenderableNode && node.bucket == RenderBucket.Forward) {
                val shader = ShaderCache.get(node.material.shader)
                shader.bind()

                val modelMatrix = node.compositeTransform.matrix
                shader["modelMatrix"] = modelMatrix
                shader["normalMatrix"] = createNormalMatrix(modelMatrix)
                bindTexture(node.material[MatKey.TextureDiffuse])

                node.render(shader)
                OpenGL.resetState() // Clean up the crap that the shader may have left behind. Could probably be done more elegant.
            }
        }

        // Let the rest of the world know that they have a chance to do forward rendering
        OpenGL.useProgram(0)
        Events.post(RenderForwardEvent())
        sceneBuffer.unbind()
        Profiler.end()

        // Now, we can apply post processing and copy everything to the screen
        Profiler.begin("Effects pipeline")
        effectsPipeline.render()
        Profiler.end()
    }

    fun bindGBuffer() {
        gBuffer.fbo.depthTexture!!.bind(0)              // gDepth
        gBuffer.fbo.getColorTexture(0).bind(1)     // gNormal
        gBuffer.fbo.getColorTexture(1).bind(2)     // gAlbedo
        gBuffer.fbo.getColorTexture(2).bind(3)     // gEmissive
    }

    private fun renderGBuffer() {
        val deltaTime = NekoApp.the.timer.deltaTime

        gBuffer.bind()
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        OpenGL.enable(GL_DEPTH_TEST)
        OpenGL.cullFace(GL_BACK)
        OpenGL.disable(GL_BLEND)

        val nodes = ArrayList<RenderableNode>()

        // Collect nodes
        scene.rootNode.scanTree { node ->
            if (node is RenderableNode && node.bucket == RenderBucket.Deferred)
                nodes.add(node)
        }

        // Sort by material
        nodes.sortBy { it.material.shader }

        // Then render the node
        Profiler.begin("GBuffer draw")
        nodes.forEach { node ->
            val shader = ShaderCache.get(node.material.shader)

            OpenGL.setBoolean(GL_CULL_FACE, node.material[MatKey.TwoSided] == true)
            bindTexture(node.material[MatKey.TextureDiffuse])
            bindTexture(node.material[MatKey.TextureNormals], 1, StaticTextures.emptyNormalMap)
            shader.bind()

            val modelMatrix = node.compositeTransform.matrix
            shader["modelMatrix"] = modelMatrix
            shader["normalMatrix"] = createNormalMatrix(modelMatrix)
            shader["specular"] = (node.material[MatKey.ColorSpecular] as? Color ?: Color.White).r
            shader["emissive"] = (node.material[MatKey.ColorEmissive] as? Color ?: Color.Black).r
            shader["shininess"] = node.material[MatKey.Shininess] as? Float ?: 4.0f
            shader["diffuseColor"] = node.material[MatKey.ColorDiffuse] as? Color ?: Color.White

            node.getComponent<Animator>()?.let {
                it.update(deltaTime)
                it.loadMatrices(shader)
            }

            node.render(shader)
        }
        Profiler.end()

        Events.post(RenderDeferredEvent())

        gBuffer.unbind()
    }

    private fun bindTexture(texture: Any?, unit: Int = 0, fallback: Texture2d = StaticTextures.white) {
        when (texture) {
            is Texture -> texture.bind(unit)
            is String -> TextureCache.get(texture).bind(unit)
            else -> fallback.bind(unit)
        }
    }

    private fun createNormalMatrix(modelMatrix: Matrix4f): Matrix3f = Matrix3f(modelMatrix).invert().transpose()

}

