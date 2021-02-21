package de.twometer.neko.res

import java.io.File

object AssetFiles {

    private val paths = ArrayList<String>()

    fun registerPath(path: String) {
        val file = File(path)
        if (!file.exists() || !file.isDirectory)
            error("Path ${file.absolutePath} not a directory")
        paths.add(file.absolutePath)
    }

    fun resolve(path: String): File {
        val file = File(path)
        if (file.isAbsolute && file.exists())
            return file

        paths.forEach {
            val candidate = File(it, path)
            if (candidate.exists())
                return candidate
        }
        error("File $path not found")
    }

}