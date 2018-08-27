package uk.co.nickthecoder.rapidragdoll

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Kill
import uk.co.nickthecoder.tickle.action.Until
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.action.animation.Fade

/**
 * When the pin is knocked over, then one of the scene's objectives have been met.
 */
class Pin : ActionRole() {

    override fun createAction(): Action {

        AbstractPlay.instance.objectives++

        return Until { (actor.direction.degrees < -70 || actor.direction.degrees > 70) }
                .then {
                    actor.event("score")
                    AbstractPlay.instance.objectives--
                }
                .then(Fade(actor.color, 1.0, 0f, Eases.easeIn))
                .then(Kill(actor))
    }

}
