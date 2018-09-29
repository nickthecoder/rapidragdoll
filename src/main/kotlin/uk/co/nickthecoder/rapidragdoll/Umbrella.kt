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

import org.jbox2d.dynamics.joints.RevoluteJointDef
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.util.Angle
import uk.co.nickthecoder.tickle.util.Attribute

class Umbrella : Fragile(), Draggable {

    @Attribute
    val angle = Angle()

    val joinPoint = Vector2d(0.0, 141.0)

    lateinit var topHalf: Actor

    override fun activated() {
        val world = actor.stage?.world

        topHalf = actor.createChild("topHalf")
        topHalf.position.y += joinPoint.y

        actor.body?.let { joinTo ->

            val jointDef = RevoluteJointDef()
            jointDef.bodyA = joinTo.jBox2DBody
            jointDef.bodyB = topHalf.body!!.jBox2DBody

            jointDef.localAnchorA = world?.pixelsToWorld(joinPoint)
            jointDef.lowerAngle = Math.toRadians(-60.0).toFloat()
            jointDef.upperAngle = Math.toRadians(60.0).toFloat()
            jointDef.enableLimit = true

            world?.jBox2dWorld?.createJoint(jointDef)
        }
        topHalf.direction.radians += angle.radians
    }

    override fun mass(): Double {
        return (actor.body?.mass ?: 0.0) + (topHalf.body?.mass ?: 0.0)
    }

}
