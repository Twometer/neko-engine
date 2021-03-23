package de.twometer.neko.gui

import com.labymedia.ultralight.plugin.filesystem.UltralightFileSystem
import de.twometer.neko.res.AssetManager
import de.twometer.neko.res.AssetType
import java.io.File
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicLong

class UltralightNekoFileSystem : UltralightFileSystem {

    private val handleCounter = AtomicLong(0)
    private val openFiles = HashMap<Long, File>()

    override fun fileExists(path: String?): Boolean {
        return path?.let { AssetManager.exists(it, AssetType.Gui) } ?: false
    }

    override fun getFileSize(handle: Long): Long {
        return openFiles[handle]?.length() ?: -1
    }

    override fun getFileMimeType(path: String?): String {
        TODO("Not yet implemented")
    }

    override fun openFile(path: String?, openForWriting: Boolean): Long {
        TODO("Not yet implemented")
    }

    override fun closeFile(handle: Long) {
        TODO("Not yet implemented")
    }

    override fun readFromFile(handle: Long, data: ByteBuffer?, length: Long): Long {
        TODO("Not yet implemented")
    }
}