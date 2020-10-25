package de.twometer.neko.event;

public class MouseButtonEvent extends NkEvent {

    public int button;

    public int action;

    public int mods;

    public MouseButtonEvent(int button, int action, int mods) {
        this.button = button;
        this.action = action;
        this.mods = mods;
    }

}
