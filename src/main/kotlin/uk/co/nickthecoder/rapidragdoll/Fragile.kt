package uk.co.nickthecoder.rapidragdoll

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Kill
import uk.co.nickthecoder.tickle.action.NoAction
import uk.co.nickthecoder.tickle.action.Until
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.action.animation.Fade
import uk.co.nickthecoder.tickle.util.Attribute

/**
 * You lose if a fragile object is knocked over!
 */
open class Fragile : ActionRole(), Draggable, Reward {

    @Attribute
    override var rewardForScene = ""

    override fun createAction(): Action {

        if (Play.instance is Victory) {
            // Don't disappear on the "Victory" scene
            return NoAction()
        } else {
            return Until { (actor.direction.degrees < -45 || actor.direction.degrees > 45) }
                    .then {
                        Play.instance.knockedFragile()
                    }
                    .then(Fade(actor.color, 1.0, 0f, Eases.easeIn))
                    .then(Kill(actor))
        }
    }

}
