package de.twometer.neko.res

import java.io.File

enum class AssetType(val folderName: String) {
    Any(""),
    Animations("animations/"),
    Textures("textures/"),
    Models("models/"),
    Shaders("shaders/"),
    Gui("gui/"),
    Sounds("sounds/"),
    Natives("natives/"),
    Raw("raw/"),
    Fonts("fonts/")
}

object AssetManager {

    private val paths = ArrayList<String>()

    fun registerPath(path: String) {
        val file = File(path)
        if (!file.exists() || !file.isDirectory)
            error("Path ${file.absolutePath} not a directory")
        paths.add(file.absolutePath)
    }

    fun exists(path: String, type: AssetType = AssetType.Any): Boolean = tryResolve(path, type) != null

    fun resolve(path: String, type: AssetType = AssetType.Any): File =
        tryResolve(path, type) ?: error("File $path not found")

    private fun tryResolve(path: String, type: AssetType = AssetType.Any): File? {
        val file = File(path)
        if (file.isAbsolute && file.exists())
            return file

        paths.forEach {
            val candidate = File(it, "${type.folderName}$path")
            if (candidate.exists())
                return candidate
        }

        return null
    }


}