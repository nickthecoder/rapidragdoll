package uk.co.nickthecoder.rapidragdoll

import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.events.KeyEvent
import uk.co.nickthecoder.tickle.resources.Resources

class Menu : Play() {

    override fun onKey(event: KeyEvent) {
        if (Resources.instance.inputs.find("escape")?.matches(event) == true) {
            Game.instance.quit()
        }
    }

}
