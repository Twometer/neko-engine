package de.twometer.neko.event;

public class MousePosEvent extends NkEvent {

    public int x;

    public int y;

    public MousePosEvent(int x, int y) {
        this.x = x;
        this.y = y;
    }

}
