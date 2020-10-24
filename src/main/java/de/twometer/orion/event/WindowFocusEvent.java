package de.twometer.orion.event;

public class WindowFocusEvent extends OrionEvent {

    public boolean focus;

    public WindowFocusEvent(boolean focus) {
        this.focus = focus;
    }

}
