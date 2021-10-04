package de.twometer.neko.scene.nodes

import de.twometer.neko.font.FontFace
import de.twometer.neko.font.Glyph
import de.twometer.neko.render.Primitives
import de.twometer.neko.render.Shader
import de.twometer.neko.scene.MatKey
import de.twometer.neko.scene.Material
import de.twometer.neko.scene.RenderBucket
import org.joml.Vector2f
import org.joml.Vector3f

class Billboard(val font: FontFace, var text: String) : RenderableNode(
    Material("Billboard", hashMapOf(Pair(MatKey.TextureDiffuse, font.texture)), "base/billboard.nks"),
    RenderBucket.Forward
) {

    companion object {
        private const val SPACE_REDUCER = 28
    }

    override fun render(shader: Shader) {
        shader["atlasSize"] = Vector2f(font.texture.width.toFloat(), font.texture.height.toFloat())
        shader["glyphColor"] = Vector3f(1f, 1f, 1f)

        var offset = -getTextWidth() / 2
        for (char in text) {
            val glyph = font.glyphs[char.toInt()] ?: continue

            renderGlyph(glyph, offset, shader)

            offset += glyph.advance + glyph.width - SPACE_REDUCER
        }
    }

    private fun renderGlyph(glyph: Glyph, offset: Float, shader: Shader) {
        // TODO: Can be instanced
        shader["glyphCoords"] = Vector2f(glyph.x.toFloat(), glyph.y.toFloat())
        shader["glyphSize"] = Vector2f(glyph.width.toFloat(), glyph.height.toFloat())
        shader["glyphOffset"] = Vector2f(glyph.xOffset + offset, -glyph.yOffset.toFloat())
        Primitives.unitQuad.render()
    }

    private fun getTextWidth(): Float {
        var width = 0f
        for (i in text.indices) {
            val char = text[i].toInt()
            val glyph = font.glyphs[char] ?: continue

            if (i != text.length - 1)
                width += glyph.width + glyph.advance - SPACE_REDUCER
        }
        return width
    }

}