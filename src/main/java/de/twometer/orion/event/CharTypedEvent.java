package de.twometer.orion.event;

public class CharTypedEvent extends OrionEvent {

    public char chr;

    public CharTypedEvent(char chr) {
        this.chr = chr;
    }

}
