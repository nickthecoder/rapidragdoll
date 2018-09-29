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

import org.jbox2d.dynamics.joints.RevoluteJoint
import org.jbox2d.dynamics.joints.RevoluteJointDef
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.AttributeType
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Until
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.action.animation.Fade
import uk.co.nickthecoder.tickle.util.Angle
import uk.co.nickthecoder.tickle.util.Attribute


class Bell : ActionRole(), Draggable {

    @Attribute(attributeType = AttributeType.RELATIVE_POSITION)
    val ceiling = Vector2d()

    @Attribute
    var angleThreshold = Angle.degrees(30.0)

    lateinit var front: Actor

    lateinit var clanger: Actor

    lateinit var rope: Actor


    override fun createAction(): Action {

        AbstractPlay.instance.objectives++

        front = actor.createChild("front")
        front.scaleXY = actor.scaleXY
        (front.role as Follower).following = actor

        createRope()

        clanger = actor.createChild("clanger")
        clanger.scaleXY = actor.scaleXY

        val world = actor.body!!.tickleWorld
        val jointDef = RevoluteJointDef()
        jointDef.bodyA = actor.body!!.jBox2DBody
        jointDef.bodyB = clanger.body!!.jBox2DBody
        jointDef.collideConnected = true

        jointDef.localAnchorA = actor.stage?.world?.pixelsToWorld(Vector2d(0.0, -20.0 * actor.scaleXY))
        world.jBox2dWorld.createJoint(jointDef) as RevoluteJoint

        if (AbstractPlay.instance is Victory) {
            // Don't disappear on the "Victory" scene
            return Until { hit() }
                    .then {
                        actor.event("ding")
                    }.forever()
        }

        return Until { hit() }
                .then {
                    actor.event("ding")
                    AbstractPlay.instance.objectives--
                }
                .then((Fade(actor.color, 1.0, 0f, Eases.easeIn))
                        .and(Fade(rope.color, 1.0, 0f, Eases.easeIn))
                        .and(Fade(front.color, 1.0, 0f, Eases.easeIn))
                        .and(Fade(clanger.color, 1.0, 0f, Eases.easeIn)))
                .then {
                    rope.die()
                    clanger.die()
                    front.die()
                    actor.die()
                }
    }

    fun createRope() {
        rope = actor.createChild("rope")
        rope.scaleXY = actor.scaleXY

        val world = actor.body!!.tickleWorld
        val jointDef = RevoluteJointDef()
        jointDef.bodyA = actor.body!!.jBox2DBody
        jointDef.bodyB = rope.body!!.jBox2DBody

        rope.tiledAppearance?.let { appearance ->
            appearance.size.y = ceiling.y / actor.scaleXY
        }
        world.jBox2dWorld.createJoint(jointDef) as RevoluteJoint

    }

    /**
     * Return true when the bell has been rung.
     * I used to test for the difference in angular velocity of the clanger and the bell.
     * The idea was to "win" the bell when the clanger hit the bell hard, but this turned out to
     * be not obvious, and tricky to do, so I've replaced it with a simple test for the angle of the bell
     * being beyond a threshold.
     */
    fun hit(): Boolean {
        //val diff = Math.abs(clanger.body!!.angularVelocity - oldAngularVelocity)
        //oldAngularVelocity = clanger.body!!.angularVelocity
        //return diff > ringThreshold
        val degrees = Math.abs(actor.direction.degrees)
        return degrees > Math.abs(angleThreshold.degrees)
    }

}
