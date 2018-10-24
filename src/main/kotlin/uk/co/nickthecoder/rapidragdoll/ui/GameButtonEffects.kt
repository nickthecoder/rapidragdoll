package uk.co.nickthecoder.rapidragdoll.ui

import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.action.animation.Scale
import uk.co.nickthecoder.tickle.util.Button
import uk.co.nickthecoder.tickle.util.ButtonEffects

class GameButtonEffects : ButtonEffects {

    override fun enter(button: Button): Action? {
        return Scale(button.actor, 0.1, button.actor.scaleXY * 1.2, Eases.easeOut)
    }

    override fun exit(button: Button): Action? {
        return Scale(button.actor, 0.1, button.actor.scaleXY / 1.2, Eases.easeIn)
    }

}
