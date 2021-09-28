package de.twometer.neko.res

import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

object RawLoader {

    fun openFile(path: String): FileInputStream {
        val filename = AssetManager.resolve(path, AssetType.Raw).absolutePath
        return FileInputStream(File(filename))
    }

    fun loadString(path: String): String {
        val reader = BufferedReader(InputStreamReader(openFile(path)))
        return reader.readText()
    }

    fun loadLines(path: String): List<String> {
        val reader = BufferedReader(InputStreamReader(openFile(path)))
        return reader.readLines()
    }

}