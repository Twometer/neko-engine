package de.twometer.orion.event;

import de.twometer.orion.util.Log;
import org.greenrobot.eventbus.EventBus;

public final class Events {

    public static void init() {
        // Create useful event bus
        EventBus.builder()
                .logNoSubscriberMessages(false)
                .sendNoSubscriberEvent(false)
                .installDefaultEventBus();
        Log.d("Event system initialized");
    }

    public static void register(Object subscriber) {
        EventBus.getDefault().register(subscriber);
    }

    public static void post(Object o) {
        EventBus.getDefault().post(o);
    }

}
