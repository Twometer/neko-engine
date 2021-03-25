package de.twometer.neko.events

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

object Events {

    fun setup() {
        EventBus.builder()
            .logNoSubscriberMessages(false)
            .sendNoSubscriberEvent(false)
            .installDefaultEventBus()
    }

    fun register(subscriber: Any) {
        if (!hasSubscriberMethods(subscriber))
            return
        EventBus.getDefault().register(subscriber)
    }

    fun unregister(subscriber: Any) {
        if (!EventBus.getDefault().isRegistered(subscriber))
            return
        EventBus.getDefault().unregister(subscriber)
    }

    fun post(event: Any) = EventBus.getDefault().post(event)

    private fun hasSubscriberMethods(subscriber: Any): Boolean {
        return subscriber.javaClass.methods.any { it.isAnnotationPresent(Subscribe::class.java) }
    }

}

data class ResizeEvent(val width: Int, val height: Int)

data class FocusEvent(val focused: Boolean)

data class CharEvent(val char: Char, val codepoint: Int)

data class KeyEvent(val key: Int, val scancode: Int, val action: Int, val mods: Int)

data class KeyPressEvent(val key: Int)

data class MouseMoveEvent(val xPos: Int, val yPos: Int)

data class MouseClickEvent(val button: Int)

data class MouseButtonEvent(val button: Int, val action: Int, val mods: Int)

data class MouseWheelEvent(val xOff: Int, val yOff: Int)

class RenderDeferredEvent

class RenderForwardEvent

class TickEvent