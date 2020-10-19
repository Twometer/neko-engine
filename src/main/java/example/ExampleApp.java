package example;

import de.twometer.orion.core.OrionApp;
import de.twometer.orion.res.ModelLoader;

public class ExampleApp extends OrionApp {

    public static void main(String[] args) {
        (new ExampleApp()).launch("Example app", 1024, 768);
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        ModelLoader.loadModels("TheSkeld.obj");
    }
}
