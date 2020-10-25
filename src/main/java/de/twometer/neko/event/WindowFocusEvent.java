package de.twometer.neko.event;

public class WindowFocusEvent extends NkEvent {

    public boolean focus;

    public WindowFocusEvent(boolean focus) {
        this.focus = focus;
    }

}
