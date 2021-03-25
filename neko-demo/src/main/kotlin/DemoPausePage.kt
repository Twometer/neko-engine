import de.twometer.neko.core.NekoApp
import de.twometer.neko.gui.Page

class DemoPausePage : Page("pause.html") {

    override fun blocksGameInput() = true

    fun exitGame() {
        NekoApp.the!!.window.close()
    }

}