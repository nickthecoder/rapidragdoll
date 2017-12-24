package uk.co.nickthecoder.rapidragdoll

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.AttributeType
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.action.movement.MoveBy
import uk.co.nickthecoder.tickle.util.Attribute

open class AnimatedInformation : ActionRole() {

    @Attribute(AttributeType.RELATIVE_POSITION)
    val moveBy = Vector2d(0.0, -300.0)

    @Attribute
    var seconds = 2.0

    open fun go() {
        action = goAction()
    }

    open fun goAction(): Action {
        return MoveBy(actor.position, moveBy, seconds, Eases.bounce3)
    }

}

class Information : AnimatedInformation() {
    override fun goAction(): Action {
        return super.goAction().then { actor.die() }
    }
}

class SceneComplete : AnimatedInformation()

class TimeIsUp : AnimatedInformation()

class YouLose : AnimatedInformation()
