package example;

import de.twometer.orion.gui.core.BindWidget;
import de.twometer.orion.gui.core.BindXml;
import de.twometer.orion.gui.core.Screen;

@BindXml("MainScreen.xml")
public class ExampleScreen extends Screen {

    @BindWidget
    public Object testButton;

    @Override
    protected void onBind() {

    }

}
