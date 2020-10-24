package de.twometer.orion.event;

public class MousePosEvent extends OrionEvent {

    public int x;

    public int y;

    public MousePosEvent(int x, int y) {
        this.x = x;
        this.y = y;
    }

}
