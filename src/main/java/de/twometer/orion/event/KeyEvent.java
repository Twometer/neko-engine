package de.twometer.orion.event;

public class KeyEvent extends OrionEvent {

    public int key;

    public int scancode;

    public int action;

    public int mods;

    public KeyEvent(int key, int scancode, int action, int mods) {
        this.key = key;
        this.scancode = scancode;
        this.action = action;
        this.mods = mods;
    }

}
