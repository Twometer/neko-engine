package de.twometer.neko.res

import de.twometer.neko.scene.Node
import mu.KotlinLogging
import org.lwjgl.assimp.*
import org.lwjgl.assimp.Assimp.*


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

                val aiMatName = AIString.calloc()
                aiGetMaterialString(aiMaterial, AI_MATKEY_NAME, 0, 0, aiMatName)
                logger.debug { "Loading material ${aiMatName.dataString()}" }
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

    private fun failure(): Nothing {
        error(aiGetErrorString() ?: "Unknown Assimp error")
    }

}