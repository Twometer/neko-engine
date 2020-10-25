package de.twometer.neko.event;

public class MouseClickedEvent extends NkEvent {

    public int button;

    public MouseClickedEvent(int button) {
        this.button = button;
    }

}
