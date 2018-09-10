/*
Rapid Rag Doll
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
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
            replaceAction(MoveBy(actor.position, Vector2d(0.0, 200.0), 1.0)
                    .and(Fade(actor.color, 1.0, 0f, Eases.easeIn))
                    .then(Kill(actor))
            )
        }
    }

    override fun endContact(contact: Contact, otherActor: Actor) {}

    abstract fun collected()
}

class CollectableGrow : Collectable() {

    @Attribute
    var factor = 1.2

    override fun collected() {
        AbstractPlay.instance.launcher?.let {
            it.scale *= factor
        }
    }
}

class CollectableShrink : Collectable() {

    @Attribute
    var factor = 1.2

    override fun collected() {
        AbstractPlay.instance.launcher?.let {
            it.scale /= factor
        }
    }
}

class CollectableSpeedUp : Collectable() {


    @Attribute
    var factor = 1.2

    override fun collected() {
        AbstractPlay.instance.launcher?.let {
            it.speed *= factor
        }
    }
}

class CollectableSlowDown : Collectable() {


    @Attribute
    var factor = 1.2

    override fun collected() {
        AbstractPlay.instance.launcher?.let {
            it.speed /= factor
        }
    }
}
