package de.twometer.orion.gui;

import de.twometer.orion.gui.core.Screen;
import de.twometer.orion.util.CrashHandler;

import java.io.IOException;

public class GuiManager {

    private Screen currentScreen;

    private final I18n i18n = new I18n();

    public GuiManager() {

    }

    public void create() {
        i18n.load();
    }

    public void showScreen(Screen screen) {
        currentScreen = screen;
        initializeScreen();
    }

    private void initializeScreen() {
        if (currentScreen != null) {
            try {
                currentScreen.load();
            } catch (IOException e) {
                CrashHandler.fatal(e);
            }
            currentScreen.onRelayout();
        }
    }

}