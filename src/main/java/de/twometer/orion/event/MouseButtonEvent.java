package de.twometer.orion.event;

public class MouseButtonEvent extends OrionEvent {

    public int button;

    public int action;

    public int mods;

    public MouseButtonEvent(int button, int action, int mods) {
        this.button = button;
        this.action = action;
        this.mods = mods;
    }

}
