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
package uk.co.nickthecoder.rapidragdoll.ui

import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.events.Input
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.util.Attribute

class LevelRoute : AbstractRole() {

    @Attribute
    var requiredScene = ""

    var cheat: Input? = null

    override fun activated() {

        cheat = Resources.instance.inputs.find("cheat")

        if (requiredScene.isNotBlank() && !Game.instance.preferences.node("scenes").node(requiredScene).getBoolean("completed", false)) {
            actor.hide()
        }
    }

    override fun tick() {
        if (cheat?.isPressed() == true) {
            actor.event("default") // Unhide
        }
    }
}
