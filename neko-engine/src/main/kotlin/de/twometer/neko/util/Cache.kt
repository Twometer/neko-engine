package de.twometer.neko.util

abstract class Cache<K, V> {

    private val map = HashMap<K, V>()

    protected abstract fun create(key: K): V

    fun preload(key: K) {
        if (!map.containsKey(key))
            map[key] = create(key)
    }

    fun get(key: K): V {
        var result = map[key]
        if (result == null) {
            result = create(key)
            map[key] = result
        }
        return result!!
    }

    fun map() = map

}