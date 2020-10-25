package de.twometer.neko.event;

public class CharTypedEvent extends NkEvent {

    public char chr;

    public int codepoint;

    public CharTypedEvent(char chr, int codepoint) {
        this.chr = chr;
        this.codepoint = codepoint;
    }

}
