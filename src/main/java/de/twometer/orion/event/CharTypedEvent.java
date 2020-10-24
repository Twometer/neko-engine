package de.twometer.orion.event;

public class CharTypedEvent extends OrionEvent {

    public char chr;

    public int codepoint;

    public CharTypedEvent(char chr, int codepoint) {
        this.chr = chr;
        this.codepoint = codepoint;
    }

}
