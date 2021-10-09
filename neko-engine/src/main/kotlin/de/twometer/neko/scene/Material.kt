package de.twometer.neko.scene

object MatKey {
    const val ColorAmbient = "_ColorAmbient"
    const val ColorDiffuse = "_ColorDiffuse"
    const val ColorEmissive = "_ColorEmissive"
    const val ColorSpecular = "_ColorSpecular"

    const val TextureAmbient = "_TextureAmbient"
    const val TextureDiffuse = "_TextureDiffuse"
    const val TextureEmissive = "_TextureEmissive"
    const val TextureSpecular = "_TextureSpecular"
    const val TextureDisplacement = "_TextureDisplacement"
    const val TextureNormals = "_TextureNormals"

    const val Reflectivity = "_Reflectivity"
    const val Opacity = "_Opacity"
    const val Shininess = "_Shininess"
    const val TwoSided = "_TwoSided"
}

data class Material(
    val name: String,
    private val props: HashMap<String, Any> = HashMap(),
    var shader: String = "base/geometry.static.nks"
) {

    companion object {
        val Default: Material
            get() = Material(
                "Default", hashMapOf(
                    MatKey.ColorDiffuse to Color(0.9f, 0.9f, 0.9f)
                )
            )
    }

    operator fun set(key: String, value: Any?) {
        if (key.isBlank())
            return

        val sanitized = sanitize(key, value)

        if (sanitized == null || (sanitized is String && sanitized.isBlank())) {
            props.remove(key)
            return
        }

        props[key] = sanitized
    }

    operator fun get(key: String): Any? = props[key]

    fun hasProperty(key: String) = props.containsKey(key)

    fun createInstance(): Material {
        return Material(name, HashMap(props), shader)
    }

    private fun sanitize(key: String, value: Any?): Any? {
        return when {
            value == null -> value
            key == MatKey.Shininess && value == 0.0f -> 16
            else -> value
        }
    }
}
