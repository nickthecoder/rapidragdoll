package uk.co.nickthecoder.rapidragdoll

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.movement.FollowMouse

class Aim : ActionRole() {

    override fun tick() {
        super.tick()
        actor.direction.degrees += 1.0
    }

    override fun createAction(): Action {
        return FollowMouse(actor.position, actor.stage?.firstView()!!)
    }

}
