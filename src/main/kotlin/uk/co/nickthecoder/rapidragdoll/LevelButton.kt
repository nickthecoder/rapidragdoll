package uk.co.nickthecoder.rapidragdoll

import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.events.MouseEvent
import uk.co.nickthecoder.tickle.util.Button

class LevelButton : Button() {

    var sceneName = ""

    override fun onClicked(event: MouseEvent) {
        Game.instance.startScene(sceneName)
    }

}