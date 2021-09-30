package de.twometer.neko.util

import org.lwjgl.opengl.GL33.*
import java.util.*

class ProfileSection {
    private val startQuery = glGenQueries()
    private val endQuery = glGenQueries()

    var duration: Double = 0.0
        private set

    fun reset() {
        duration = 0.0
    }

    fun runStartQuery() = glQueryCounter(startQuery, GL_TIMESTAMP)

    fun runEndQuery() = glQueryCounter(endQuery, GL_TIMESTAMP)

    fun retrieveData() {
        while (glGetQueryObjectui(endQuery, GL_QUERY_RESULT_AVAILABLE) != GL_TRUE) {
            // wait
        }

        val startTime = glGetQueryObjectui64(startQuery, GL_QUERY_RESULT)
        val endTime = glGetQueryObjectui64(endQuery, GL_QUERY_RESULT)
        val durationNs = (endTime - startTime)
        duration = durationNs / 1.0E6
    }

}

object Profiler {

    private var enabled = false
    private val profile = HashMap<String, ProfileSection>()
    private val sectionStack = Stack<String>()

    fun enable() {
        enabled = true
    }

    fun disable() {
        enabled = false
    }

    fun begin(name: String) {
        if (!enabled) return

        var entry = profile[name]
        if (entry == null) {
            entry = ProfileSection()
            profile[name] = entry
        }
        sectionStack.push(name)
        entry.runStartQuery()
    }

    fun end() {
        if (!enabled) return

        profile[sectionStack.pop()]!!.runEndQuery()
    }

    fun beginFrame() {
        if (!enabled) return

        for (section in profile)
            section.value.reset()
    }

    fun endFrame() {
        if (!enabled) return

        for (section in profile)
            section.value.retrieveData()
    }

    fun getSections() = Collections.unmodifiableSet(profile.entries)

}
