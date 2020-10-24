package example;

import de.twometer.orion.core.OrionApp;
import de.twometer.orion.gui.Page;
import de.twometer.orion.util.Log;

public class MainPage extends Page {

    public String test = "yee boiii";

    public MainPage() {
        super("MainPage.html");
    }

    @Override
    public void onDomReady() {
        context.setElementText("header", "Hello");
    }

    public void closeClicked() {
        OrionApp.get().getGuiManager().showPage(null);
    }

    public void testButtonClicked() {
        Log.i("Test button clicked!");
        context.call("Helo", "yee", "test", "lol", new Object());
    }
}
