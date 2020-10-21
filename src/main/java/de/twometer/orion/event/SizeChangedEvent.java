package de.twometer.orion.event;

public class SizeChangedEvent extends OrionEvent {

    public int width;

    public int height;

    public SizeChangedEvent(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
