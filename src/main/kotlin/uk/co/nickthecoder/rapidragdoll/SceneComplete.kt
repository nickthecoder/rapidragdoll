package uk.co.nickthecoder.rapidragdoll

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Until
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.action.movement.MoveBy

class SceneComplete : ActionRole() {

    var complete: Boolean = false

    override fun createAction(): Action {
        return Until { complete }
                .then(MoveBy(actor.position, Vector2d(0.0, -300.0), 2.0, Eases.bounce3))
                .then { Play.instance.sceneComplete = true }
    }
}
