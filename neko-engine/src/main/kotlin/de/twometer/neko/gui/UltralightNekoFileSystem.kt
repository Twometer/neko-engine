package de.twometer.neko.gui

import com.labymedia.ultralight.plugin.filesystem.UltralightFileSystem
import com.labymedia.ultralight.plugin.filesystem.UltralightFileSystem.INVALID_FILE_HANDLE
import de.twometer.neko.res.AssetManager
import de.twometer.neko.res.AssetType
import java.io.File
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.file.Files
import java.util.concurrent.atomic.AtomicLong

class UltralightNekoFileSystem : UltralightFileSystem {

    private val handleCounter = AtomicLong(0)
    private val openFiles = HashMap<Long, FileHandle>()

    data class FileHandle(val file: File, val stream: InputStream)

    override fun fileExists(path: String): Boolean {
        return AssetManager.exists(path, AssetType.Gui)
    }

    override fun getFileSize(handle: Long): Long {
        return openFiles[handle]?.file?.length() ?: -1
    }

    override fun getFileMimeType(path: String): String? {
        if (!fileExists(path))
            return null
        return Files.probeContentType(AssetManager.resolve(path, AssetType.Gui).toPath())
    }

    override fun openFile(path: String, openForWriting: Boolean): Long {
        if (!fileExists(path))
            return INVALID_FILE_HANDLE

        val file = AssetManager.resolve(path, AssetType.Gui)
        val handle = handleCounter.incrementAndGet()
        openFiles[handle] = FileHandle(file, file.inputStream())
        return handle
    }

    override fun closeFile(handle: Long) {
        openFiles[handle]?.stream?.close()
        openFiles.remove(handle)
    }

    override fun readFromFile(handle: Long, data: ByteBuffer, length: Long): Long {
        val fileHandle = openFiles[handle] ?: return -1

        val array = ByteArray(length.toInt())
        val read = fileHandle.stream.read(array, 0, length.toInt())
        array.forEach { data.put(it) }

        return read.toLong()
    }
}