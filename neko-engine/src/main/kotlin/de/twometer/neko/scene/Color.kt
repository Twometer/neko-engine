package de.twometer.neko.scene

data class Color(val r: Float, val g: Float, val b: Float, val a: Float = 1.0f) {
    companion object {
        val Transparent = Color(0f, 0f, 0f, 0f)
        val Black = Color(0f, 0f, 0f, 1f)
        val White = Color(1f, 1f, 1f, 1f)
        val Red = Color(1f, 0f, 0f, 1f)
        val Green = Color(0f, 1f, 0f, 1f)
        val Blue = Color(0f, 0f, 1f, 1f)
    }
}