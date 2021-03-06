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
package uk.co.nickthecoder.rapidragdoll.roles

import org.jbox2d.dynamics.contacts.Contact
import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Delay
import uk.co.nickthecoder.tickle.action.EventAction
import uk.co.nickthecoder.tickle.physics.ContactListenerRole
import uk.co.nickthecoder.tickle.util.Attribute

class Plasma : ActionRole(), ContactListenerRole, Switchable {

    @Attribute
    override var name: String = ""

    private var on = true

    override fun switch(value: Boolean) {
        on = value
        if (on) {
            actor.event("on")
            replaceAction(createAction())
        } else {
            actor.event("off")
            replaceAction(null)
        }
    }

    override fun createAction(): Action {
        return EventAction(actor, "default")
                .then(Delay(0.05))
                .forever()
    }

    override fun beginContact(contact: Contact, otherActor: Actor) {
        if (on) {
            val otherRole = otherActor.role
            if (otherRole is DollPart) {
                otherRole.doll.zapped()
            }
        }
    }

    override fun endContact(contact: Contact, otherActor: Actor) {}
}
