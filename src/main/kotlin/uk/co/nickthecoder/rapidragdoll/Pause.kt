package uk.co.nickthecoder.rapidragdoll

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.AttributeType
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Until
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.action.movement.MoveBy
import uk.co.nickthecoder.tickle.util.Attribute

class Pause : ActionRole() {

    @Attribute(AttributeType.RELATIVE_POSITION)
    val moveBy = Vector2d(0.0, -300.0)

    @Attribute
    var seconds = 2.0

    override fun createAction(): Action {

        val backAgain = Vector2d(moveBy).mul(-1.0)

        return Until { Play.instance.paused }
                .then(MoveBy(actor.position, moveBy, seconds, Eases.easeOut))
                .then(Until { !Play.instance.paused })
                .then(MoveBy(actor.position, backAgain, seconds, Eases.easeIn))
                .forever()
    }

}
