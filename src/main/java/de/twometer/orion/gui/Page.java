package de.twometer.orion.gui;

public class Page {

    private final String path;

    public Page(String path) {
        this.path = path;
    }

    public boolean isCursorVisible() {
        return true;
    }

    public String getPath() {
        return path;
    }
}
