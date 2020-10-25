package de.twometer.neko.event;

public class SizeChangedEvent extends NkEvent {

    public int width;

    public int height;

    public SizeChangedEvent(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
