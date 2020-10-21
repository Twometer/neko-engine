package de.twometer.orion.event;

public class MouseClickedEvent extends OrionEvent {

    public int button;

    public MouseClickedEvent(int button) {
        this.button = button;
    }

}
