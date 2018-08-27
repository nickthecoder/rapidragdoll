package uk.co.nickthecoder.rapidragdoll

import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.events.KeyEvent
import uk.co.nickthecoder.tickle.resources.Resources

class Menu : AbstractPlay() {

    override fun onKey(event: KeyEvent) {
        if (Resources.instance.inputs.find("escape")?.matches(event) == true) {
            if (menuName.isBlank()) {
                Game.instance.quit()
            } else {
                Game.instance.startScene(menuName)
            }
        }
    }

}
