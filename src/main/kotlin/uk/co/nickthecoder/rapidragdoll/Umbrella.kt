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

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.physics.TicklePinJoint
import uk.co.nickthecoder.tickle.util.Angle
import uk.co.nickthecoder.tickle.util.Attribute

class Umbrella : Fragile(), Draggable {

    @Attribute
    val angle = Angle()

    lateinit var topHalf: Actor

    override fun activated() {

        val joinPoint = Vector2d(0.0, 141.0 * actor.scaleXY)

        topHalf = actor.createChild("topHalf").apply {
            scaleXY = actor.scaleXY
            position.y += joinPoint.y
            direction.radians += angle.radians
        }

        TicklePinJoint(actor, topHalf, joinPoint).limitRotation(Angle.degrees(-60.0), Angle.degrees(60.0))
    }

    override fun mass(): Double {
        return (actor.body?.mass ?: 0.0) + (topHalf.body?.mass ?: 0.0)
    }

}
