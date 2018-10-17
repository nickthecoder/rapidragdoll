package uk.co.nickthecoder.rapidragdoll.ui

import uk.co.nickthecoder.rapidragdoll.Play
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.events.MouseEvent
import uk.co.nickthecoder.tickle.util.Button

class Exit : Button() {

    override fun onClicked(event: MouseEvent) {
        val director = Game.instance.director
        if (director is Play) {
            Game.instance.startScene(director.menuName)
        } else {
            Game.instance.startScene("menu")
        }
    }

}
