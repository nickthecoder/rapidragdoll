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
import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.NinePatchAppearance
import uk.co.nickthecoder.tickle.physics.TickleMouseJoint

/**
 * An elastic line between the Hand and the item being dragged.
 * Used on the Victory (Doll House) scenes to pick up [Doll]s, and other [Draggable] items.
 */
class Elastic : AbstractRole() {

    var mouseJoint: TickleMouseJoint? = null
        set(v) {
            field = v
            if (v == null) {
                actor.hide()
            } else {
                actor.event("show")
            }
        }

    private val from = Vector2d()

    private val to = Vector2d()

    override fun tick() {
        mouseJoint?.let { mouseJoint ->

            actor.stage?.firstView()?.mousePosition()?.let { mouseJoint.target(it) }

            val app = actor.appearance
            if (app is NinePatchAppearance) {
                mouseJoint.anchorA(from)
                mouseJoint.anchorB(to)

                actor.position.set(from)
                app.lineTo(to)
            }

        }
    }

}
