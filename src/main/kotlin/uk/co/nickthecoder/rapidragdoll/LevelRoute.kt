package uk.co.nickthecoder.rapidragdoll

import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.util.Attribute
import uk.co.nickthecoder.tickle.util.Button

class LevelRoute : Button() {

    @Attribute
    var requiredScene = ""

    override fun activated() {
        if (!Game.instance.preferences.node("scenes").node(requiredScene).getBoolean("completed", false)) {
            actor.hide()
        }
    }

}
