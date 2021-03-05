package de.twometer.neko.util

class Timer(tps: Int) {

    private val delay: Int = 1000 / tps
    private var lastReset = System.currentTimeMillis()

    fun reset() =
        System.currentTimeMillis().also { lastReset = it }

    fun elapsed(): Boolean = System.currentTimeMillis() - lastReset > delay

    fun getPartial(): Double = 1.0 - ((lastReset + delay - System.currentTimeMillis()) / delay)

}