package de.twometer.neko.scene

object MatKey {
    const val ColorAmbient = "_ColorAmbient"
    const val ColorDiffuse = "_ColorDiffuse"
    const val ColorEmissive = "_ColorEmissive"
    const val ColorSpecular = "_ColorSpecular"

    const val TextureAmbient = "_TextureAmbient"
    const val TextureDiffuse = "_TextureDiffuse"
    const val TextureDisplacement = "_TextureDisplacement"
    const val TextureEmissive = "_TextureEmissive"
    const val TextureNormals = "_TextureNormals"

    const val Reflectivity = "_Reflectivity"
    const val RefractionIndex = "_RefractionIndex"
    const val Opacity = "_Opacity"
    const val Shininess = "_Shininess"
}

data class Material(val name: String, private val props: HashMap<String, Any> = HashMap()) {

    operator fun set(key: String, value: Any) {
        if (key.isBlank())
            return

        if (value is String && value.isBlank()) {
            props.remove(key)
            return
        }

        props[key] = value
    }

    operator fun get(key: String): Any? = props[key]

    fun hasProperty(key: String) = props.containsKey(key)
}
