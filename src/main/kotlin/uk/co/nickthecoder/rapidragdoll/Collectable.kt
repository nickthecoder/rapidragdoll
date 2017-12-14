package uk.co.nickthecoder.rapidragdoll

import org.jbox2d.dynamics.contacts.Contact
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Kill
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.action.animation.Fade
import uk.co.nickthecoder.tickle.action.movement.Circle
import uk.co.nickthecoder.tickle.action.movement.MoveBy
import uk.co.nickthecoder.tickle.physics.ContactListenerRole
import uk.co.nickthecoder.tickle.util.Attribute

abstract class Collectable : ActionRole(), ContactListenerRole {

    @Attribute
    var radius = 30.0

    @Attribute
    var period = 2.0

    var collected = false

    override fun createAction(): Action {
        return Circle(actor.position, radius, period).forever()
    }

    override fun beginContact(contact: Contact, otherActor: Actor) {
        val otherRole = otherActor.role
        if (!collected && otherRole is DollPart) {
            collected = true
            collected()
            action = MoveBy(actor.position, Vector2d(0.0, 200.0), 1.0)
                    .and(Fade(actor.color, 1.0, 0f, Eases.easeIn))
                    .then(Kill(actor))
        }
    }

    override fun endContact(contact: Contact, otherActor: Actor) {}

    abstract fun collected()
}

class CollectableGrow : Collectable() {

    @Attribute
    var factor = 1.2

    override fun collected() {
        Play.instance.launcher?.let {
            it.scale *= factor
        }
    }
}

class CollectableShrink : Collectable() {

    @Attribute
    var factor = 1.2

    override fun collected() {
        Play.instance.launcher?.let {
            it.scale /= factor
        }
    }
}

class CollectableSpeedUp : Collectable() {


    @Attribute
    var factor = 1.2

    override fun collected() {
        Play.instance.launcher?.let {
            it.speed *= factor
        }
    }
}

class CollectableSlowDown : Collectable() {


    @Attribute
    var factor = 1.2

    override fun collected() {
        Play.instance.launcher?.let {
            it.speed /= factor
        }
    }
}
