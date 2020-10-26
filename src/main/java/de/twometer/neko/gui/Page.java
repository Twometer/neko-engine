package de.twometer.neko.gui;

public abstract class Page {

    private final String path;

    protected PageContext context;

    public Page(String path) {
        this.path = path;
    }

    public boolean isCursorVisible() {
        return true;
    }

    public String getPath() {
        return path;
    }

    public void onDomReady() {
    }

    public void onUnload() {
    }

    public void setContext(PageContext context) {
        this.context = context;
    }
}
