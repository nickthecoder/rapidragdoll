package uk.co.nickthecoder.rapidragdoll

import org.jbox2d.dynamics.contacts.Contact
import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Delay
import uk.co.nickthecoder.tickle.action.Do
import uk.co.nickthecoder.tickle.physics.ContactListenerRole

class Plasma : ActionRole(), ContactListenerRole {

    var frame = 0

    override fun createAction(): Action {
        return Do {
            actor.event("default")
        }
                .then(Delay(0.05))
                .forever()
    }

    override fun beginContact(contact: Contact, otherActor: Actor) {
        val otherRole = otherActor.role
        if (otherRole is DollPart) {
            otherRole.doll.zapped()
        }
    }

    override fun endContact(contact: Contact, otherActor: Actor) {}
}
