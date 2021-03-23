package de.twometer.neko.gui

import com.labymedia.ultralight.UltralightJava
import com.labymedia.ultralight.UltralightPlatform
import com.labymedia.ultralight.config.FontHinting
import com.labymedia.ultralight.config.UltralightConfig
import de.twometer.neko.res.AssetManager
import de.twometer.neko.res.AssetType
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Paths

private val logger = KotlinLogging.logger {}

object UltralightLoader {

    fun load() {
        logger.info { "Loading ultralight..." }

        val ultralightPath = AssetManager.resolve("./ultralight", AssetType.Natives).toPath()
        val resourcePath = Paths.get(ultralightPath.toString(), "resources")

        if (!Files.exists(ultralightPath))
            error("Ultralight natives not found")

        UltralightJava.extractNativeLibrary(ultralightPath)
        UltralightJava.load(ultralightPath)

        val platform = UltralightPlatform.instance()
        platform.setConfig(
            UltralightConfig()
                .fontHinting(FontHinting.NORMAL)
                .resourcePath(resourcePath.toString())
                .deviceScale(1.0)
        )
        platform.usePlatformFontLoader()
        platform.setLogger(UltralightNekoLogger())
        platform.setFileSystem(UltralightNekoFileSystem())
        platform.setClipboard(UltralightNekoClipboard())
    }

}