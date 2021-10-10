package de.twometer.neko.gui

import com.labymedia.ultralight.UltralightJava
import com.labymedia.ultralight.UltralightLoadException
import com.labymedia.ultralight.UltralightPlatform
import com.labymedia.ultralight.config.FontHinting
import com.labymedia.ultralight.config.UltralightConfig
import de.twometer.neko.core.NekoApp
import de.twometer.neko.res.AssetManager
import de.twometer.neko.res.AssetType
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Paths

private val logger = KotlinLogging.logger {}

object UltralightLoader {

    private val window = NekoApp.the.window

    fun load() {
        logger.info { "Loading ultralight..." }

        val ultralightPath = AssetManager.resolve("./ultralight", AssetType.Natives).toPath()
        val resourcePath = Paths.get(ultralightPath.toString(), "resources")

        if (!Files.exists(ultralightPath))
            error("Ultralight natives not found")

        try {
            UltralightJava.extractNativeLibrary(ultralightPath)
        } catch (e: UltralightLoadException) {
            if (e.cause is java.nio.file.AccessDeniedException)
                logger.warn { "Write access to the Ultralight natives directory is denied. This is probably due to the app already running, ignoring." }
            else throw e
        }

        UltralightJava.load(ultralightPath)

        val platform = UltralightPlatform.instance()
        platform.setConfig(
            UltralightConfig()
                .fontHinting(FontHinting.SMOOTH)
                .resourcePath(resourcePath.toString())
                .deviceScale(window.getScale().toDouble())
        )
        platform.usePlatformFontLoader()
        platform.setLogger(UltralightNekoLogger())
        platform.setFileSystem(UltralightNekoFileSystem())
        platform.setClipboard(UltralightNekoClipboard())
    }

}