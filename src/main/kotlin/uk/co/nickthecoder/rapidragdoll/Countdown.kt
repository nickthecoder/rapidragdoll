package uk.co.nickthecoder.rapidragdoll

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.action.Delay
import uk.co.nickthecoder.tickle.action.NoAction
import uk.co.nickthecoder.tickle.util.Attribute

class Countdown : ActionRole() {

    @Attribute
    var seconds = 30

    override fun activated() {
        updateText()
    }

    fun go() {
        action = Delay(1.0).then { countdown() }.repeat(seconds).then { Play.instance.timeIsUp() }
    }

    fun stop() {
        action = NoAction()
    }

    fun countdown() {
        seconds--
        updateText()
    }

    fun updateText() {
        actor.textAppearance?.text = timeString(seconds)
    }
}
