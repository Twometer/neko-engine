package de.twometer.orion.event;

public class ScrollEvent extends OrionEvent {

    public int xDelta;

    public int yDelta;

    public ScrollEvent(int xDelta, int yDelta) {
        this.xDelta = xDelta;
        this.yDelta = yDelta;
    }

}
