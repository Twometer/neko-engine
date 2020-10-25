package example;

import de.twometer.neko.core.NekoApp;
import de.twometer.neko.gui.Page;
import de.twometer.neko.util.Log;

public class MainPage extends Page {

    // All variables are automatically exposed to JS
    public String test = "yee boiii";

    // Define the main HTML for this page
    public MainPage() {
        super("MainPage.html");
    }

    // Gets called after the DOM was loaded but before the JavaScript OnLoaded() is called
    @Override
    public void onDomReady() {
        // Modify HTML text
        context.setElementText("header", "Hello");

        // Get html text
        Log.i(context.getElementProperty("header", "innerText"));
    }

    // All methods in this class are automatically exposed to JavaScript
    public void closeClicked() {
        NekoApp.get().getGuiManager().showPage(null);
    }

    public void testButtonClicked() {
        Log.i("Test button clicked!");
        // Call a JS function
        var retval = context.call("Helo", "yee", "test", "lol", new Object());
        // And get its return value
        Log.i(retval);
    }

    public void test2ButtonClicked() {
        test = "another test"; // You can change variables and the changes will be synchronized to the JS engine
        context.call("SaySomething");
        Log.i(test); // The "reply" from JS will also be synchronized
    }
}
