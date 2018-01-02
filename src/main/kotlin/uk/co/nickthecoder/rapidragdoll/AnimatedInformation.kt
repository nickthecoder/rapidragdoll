package uk.co.nickthecoder.rapidragdoll

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.AttributeType
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.action.movement.MoveBy
import uk.co.nickthecoder.tickle.resources.Resources
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
        // Adjust the "moveBy" to account for window resizing. moveBy is suitable for the size defined in game info.
        val view = actor.stage?.firstView()!!
        val gi = Resources.instance.gameInfo
        moveBy.mul(view.rect.width.toDouble() / gi.width, view.rect.height.toDouble() / gi.height)
        // Now it is suitable for the current window size.

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
