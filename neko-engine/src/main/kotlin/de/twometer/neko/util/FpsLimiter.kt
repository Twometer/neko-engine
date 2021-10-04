package de.twometer.neko.util

import kotlin.math.roundToLong

object FpsLimiter {

    private const val NS_PER_SECOND = 1.0E9
    private var prevFrame = System.nanoTime()

    init {
        val precisionThread = Thread {
            while (true)
                Thread.sleep(Long.MAX_VALUE)
        }
        precisionThread.isDaemon = true
        precisionThread.start()
    }

    fun sync(fps: Int) {
        if (fps <= 0) return
        val frameTime = System.nanoTime() - prevFrame

        val minFrameTimeNs = NS_PER_SECOND / fps
        val timeToWait = (minFrameTimeNs - frameTime).roundToLong()
        if (timeToWait > 0)
            preciseWait(timeToWait)

        prevFrame = System.nanoTime()
    }

    private fun preciseWait(durationNs: Long) {
        val startTime = System.nanoTime()
        val endTime = startTime + durationNs

        while (System.nanoTime() < endTime)
            Thread.yield()
    }

}