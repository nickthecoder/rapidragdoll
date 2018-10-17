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

import org.joml.Vector2d
import uk.co.nickthecoder.rapidragdoll.AbstractPlay
import uk.co.nickthecoder.tickle.AbstractRole

class Arrow : AbstractRole() {

    private val mouse = Vector2d()
    private val tempVector = Vector2d()

    override fun tick() {
        actor.stage?.firstView()?.mousePosition(mouse)
        AbstractPlay.instance.launcher?.let {
            actor.position.set(it.actor.position)
            mouse.sub(actor.position, tempVector)
            val magnitude = tempVector.length()

            if (magnitude > it.speed) {
                // Show the Aim actor, and draw the arrow from the launcher towards the mouse position
                // With magnitude of the maximum speed of the launcher.
                AbstractPlay.instance.aim?.actor?.event("default")
                tempVector.normalize(it.speed).add(actor.position)
                actor.ninePatchAppearance?.lineTo(tempVector)
            } else {
                // Hide the Aim actor, and draw the arrow from the launcher to the mouse position
                AbstractPlay.instance.aim?.actor?.hide()
                actor.ninePatchAppearance?.lineTo(mouse)
            }
        }
    }

}
