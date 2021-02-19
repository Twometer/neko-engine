package neko_demo

import de.twometer.neko.core.AppConfig
import de.twometer.neko.core.NekoApp

class DemoApp : NekoApp(AppConfig(windowTitle = "Neko Engine Demo")) {


}

fun main() {
    DemoApp().run()
}