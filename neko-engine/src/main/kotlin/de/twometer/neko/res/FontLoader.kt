package de.twometer.neko.res

import de.twometer.neko.font.FontFace
import de.twometer.neko.font.Glyph
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

object FontLoader {

    fun load(name: String): FontFace {
        logger.info { "Loading font {$name.fnt, $name.png}" }

        val fntData = AssetManager.resolve("$name.fnt", AssetType.Fonts).readLines()
        val imgPath = AssetManager.resolve("$name.png", AssetType.Fonts).absolutePath

        val image = ImageLoader.load(imgPath)
        val texture = TextureLoader.load(image.pixels, image.width, image.height, false)
        image.destroy()

        return FontFace(texture, parse(fntData))
    }

    private fun parse(fntData: List<String>): Map<Int, Glyph> {
        val map = HashMap<Int, Glyph>()
        fntData.filter(this::isGlyph).forEach {
            parseGlyph(it).also { glyph ->
                map[glyph.id] = glyph
            }
        }
        return map
    }

    private fun isGlyph(line: String): Boolean = line.startsWith("char ")

    private fun parseGlyph(line: String): Glyph = Glyph(
        extractProperty(line, "id"),
        extractProperty(line, "x"),
        extractProperty(line, "y"),
        extractProperty(line, "width"),
        extractProperty(line, "height"),
        extractProperty(line, "xoffset"),
        extractProperty(line, "yoffset"),
        extractProperty(line, "xadvance")
    )

    private fun extractProperty(line: String, property: String): Int {
        val key = "$property="
        return line.substringAfter(key).substringBefore(" ").toInt()
    }

}