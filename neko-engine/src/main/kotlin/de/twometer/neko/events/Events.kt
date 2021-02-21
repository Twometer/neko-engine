package de.twometer.neko.events

import org.greenrobot.eventbus.EventBus

object Events {

    fun setup() {
        EventBus.builder()
            .logNoSubscriberMessages(false)
            .sendNoSubscriberEvent(false)
            .installDefaultEventBus()
    }

    fun register(subscriber: Any) = EventBus.getDefault().register(subscriber)

    fun unregister(subscriber: Any) = EventBus.getDefault().unregister(subscriber)

    fun post(event: Any) = EventBus.getDefault().post(event)

}

data class ResizeEvent(val width: Int, val height: Int)