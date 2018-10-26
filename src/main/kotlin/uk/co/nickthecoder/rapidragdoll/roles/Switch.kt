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
import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Role
import uk.co.nickthecoder.tickle.physics.ContactListenerRole
import uk.co.nickthecoder.tickle.stage.findRoles
import uk.co.nickthecoder.tickle.util.Attribute

interface Switchable : Role {
    var name: String
    fun switch(value: Boolean)
}

class Switch : AbstractRole(), ContactListenerRole {

    @Attribute
    var on: Boolean = true

    @Attribute
    var controls: String = ""

    override fun activated() {
        super.activated()
        actor.event(if (on) "on" else "off")
        actor.stage?.findRoles<Switchable>()?.firstOrNull { it.name == controls }?.switch(on)
    }

    override fun tick() {
    }

    override fun beginContact(contact: Contact, otherActor: Actor) {
        val otherRole = otherActor.role
        if (otherRole is DollPart) {
            on = !on
            actor.event(if (on) "on" else "off")
            actor.stage?.findRoles<Switchable>()?.firstOrNull { it.name == controls }?.switch(on)
        }
    }

    override fun endContact(contact: Contact, otherActor: Actor) {}
}
