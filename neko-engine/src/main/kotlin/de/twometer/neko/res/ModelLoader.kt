package de.twometer.neko.res

import de.twometer.neko.scene.Color
import de.twometer.neko.scene.MatKey
import de.twometer.neko.scene.Material
import de.twometer.neko.scene.Node
import mu.KotlinLogging
import org.lwjgl.PointerBuffer
import org.lwjgl.assimp.*
import org.lwjgl.assimp.Assimp.*
import java.nio.IntBuffer


private val logger = KotlinLogging.logger {}

object ModelLoader {

    fun loadFromFile(path: String): Node {
        logger.info { "Loading model $path" }

        val file = AssetManager.resolve(path)
        val flags = aiProcess_Triangulate or
                aiProcess_JoinIdenticalVertices or
                aiProcess_RemoveRedundantMaterials or
                aiProcess_ImproveCacheLocality or
                aiProcess_FixInfacingNormals or
                aiProcess_FindInvalidData or
                aiProcess_LimitBoneWeights or
                aiProcess_SortByPType or
                aiProcess_GenBoundingBoxes or
                aiProcess_GenSmoothNormals or
                aiProcess_GenUVCoords
        val aiScene = aiImportFile(file.absolutePath, flags) ?: failure()

        val node = Node()

        aiScene.mAnimations()?.also {
            val aiNumAnimations = aiScene.mNumAnimations()
            for (i in 0 until aiNumAnimations) {
                val aiAnimation = AIAnimation.create(it[i])
                logger.debug { "Loading animation ${aiAnimation.mName().dataString()}" }
            }
        }

        aiScene.mMaterials()?.also {
            val aiNumMaterials = aiScene.mNumMaterials()
            for (i in 0 until aiNumMaterials) {
                val aiMaterial = AIMaterial.create(it[i])
                val material = createMaterial(aiMaterial)
                println(material.toString())
            }
        }

        aiScene.mMeshes()?.also {
            val aiNumMeshes = aiScene.mNumMeshes()
            for (i in 0 until aiNumMeshes) {
                val aiMesh = AIMesh.create(it[i])
                logger.debug {
                    "Loading mesh ${
                        aiMesh.mName().dataString()
                    } (${aiMesh.mNumVertices()} vertices, ${aiMesh.mNumFaces()} tris, ${aiMesh.mNumBones()} bones)"
                }
            }
        }

        return node
    }

    private fun createMaterial(aiMaterial: AIMaterial): Material {
        val aiMatName = AIString.calloc()
        aiGetMaterialString(aiMaterial, AI_MATKEY_NAME, 0, 0, aiMatName)

        val material = Material(aiMatName.dataString())
        logger.debug { "Loading material ${material.name}" }

        material[MatKey.ColorAmbient] = aiMaterial.getColor(AI_MATKEY_COLOR_AMBIENT)
        material[MatKey.ColorDiffuse] = aiMaterial.getColor(AI_MATKEY_COLOR_DIFFUSE)
        material[MatKey.ColorEmissive] = aiMaterial.getColor(AI_MATKEY_COLOR_EMISSIVE)
        material[MatKey.ColorSpecular] = aiMaterial.getColor(AI_MATKEY_COLOR_SPECULAR)

        material[MatKey.TextureAmbient] = aiMaterial.getTexture(aiTextureType_AMBIENT)
        material[MatKey.TextureDiffuse] = aiMaterial.getTexture(aiTextureType_DIFFUSE)
        material[MatKey.TextureDisplacement] = aiMaterial.getTexture(aiTextureType_DISPLACEMENT)
        material[MatKey.TextureEmissive] = aiMaterial.getTexture(aiTextureType_EMISSIVE)
        material[MatKey.TextureNormals] = aiMaterial.getTexture(aiTextureType_NORMALS)

        material[MatKey.Reflectivity] = aiMaterial.getFloat(AI_MATKEY_REFLECTIVITY)
        material[MatKey.Opacity] = aiMaterial.getFloat(AI_MATKEY_OPACITY)
        material[MatKey.RefractionIndex] = aiMaterial.getFloat(AI_MATKEY_REFRACTI)
        material[MatKey.Shininess] = aiMaterial.getFloat(AI_MATKEY_SHININESS)

        return material
    }

    private fun failure(): Nothing {
        error(aiGetErrorString() ?: "Unknown Assimp error")
    }

    private fun AIMaterial.getColor(matKey: String): Color {
        val color = AIColor4D.create()
        aiGetMaterialColor(this, matKey, aiTextureType_NONE, 0, color)
        return Color(color.r(), color.g(), color.b(), color.a())
    }

    private fun AIMaterial.getTexture(textureType: Int): String {
        val path = AIString.calloc()
        aiGetMaterialTexture(this, textureType, 0, path, null as IntBuffer?, null, null, null, null, null)
        return path.dataString()
    }

    private fun AIMaterial.getFloat(matKey: String): Float {
        val pointerBuffer = PointerBuffer.allocateDirect(1)
        aiGetMaterialProperty(this, matKey, pointerBuffer)
        val property = AIMaterialProperty.create(pointerBuffer.get())

        // Ignore null pointer addresses
        if (property.address() == 0L) return 0F

        // Read the float
        val floatBuffer = property.mData().asFloatBuffer()
        return floatBuffer.get()
    }

}