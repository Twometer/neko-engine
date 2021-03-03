package de.twometer.neko.core

data class AppConfig(
    val windowWidth: Int = 1024,
    val windowHeight: Int = 768,
    val windowTitle: String = "Neko Engine",
    val fullscreen: Boolean = false,
    val glMajor: Int = 3,
    val glMinor: Int = 3,
    val timerSpeed: Int = 30,
    val debugMode: Boolean = false
)
