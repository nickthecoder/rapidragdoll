package uk.co.nickthecoder.rapidragdoll

import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.Actor

class Follower : AbstractRole() {

    var following: Actor? = null

    override fun tick() {
        following?.let {
            actor.position.set(it.position)
            actor.direction.radians = it.direction.radians
        }
    }

}
