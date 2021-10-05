package de.twometer.neko.res

import de.twometer.neko.scene.Animation
import de.twometer.neko.util.Cache

object AnimationCache : Cache<String, Animation>() {

    override fun create(key: String): Animation = AnimationLoader.loadAnimation(key)

}