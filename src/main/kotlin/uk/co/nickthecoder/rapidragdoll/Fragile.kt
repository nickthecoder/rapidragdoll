package uk.co.nickthecoder.rapidragdoll

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Kill
import uk.co.nickthecoder.tickle.action.Until
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.action.animation.Fade

/**
 * You lose if a frgaile object is knocked over!
 */
open class Fragile : ActionRole(), Draggable {

    override fun createAction(): Action {

        return Until { (actor.direction.degrees < -45 || actor.direction.degrees > 45) }
                .then {
                    Play.instance.knockedFragile()
                }
                .then(Fade(actor.color, 1.0, 0f, Eases.easeIn))
                .then(Kill(actor))
    }

}
