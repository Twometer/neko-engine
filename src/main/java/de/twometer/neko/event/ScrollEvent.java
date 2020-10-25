package de.twometer.neko.event;

public class ScrollEvent extends NkEvent {

    public int xDelta;

    public int yDelta;

    public ScrollEvent(int xDelta, int yDelta) {
        this.xDelta = xDelta;
        this.yDelta = yDelta;
    }

}
