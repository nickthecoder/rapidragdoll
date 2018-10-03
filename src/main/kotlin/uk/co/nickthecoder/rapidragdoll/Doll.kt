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

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Kill
import uk.co.nickthecoder.tickle.action.ParallelAction
import uk.co.nickthecoder.tickle.action.Until
import uk.co.nickthecoder.tickle.action.animation.Fade
import uk.co.nickthecoder.tickle.physics.TicklePinJoint
import uk.co.nickthecoder.tickle.util.Attribute
import uk.co.nickthecoder.tickle.util.CostumeAttribute

/**
 * A Doll is a created in two ways :
 * 1) By a Launcher when solving a regular level
 * 2) Added to the scene from within the SceneEditor.
 * The later is usually done from a [Victory] scene, i.e. the Doll's House, but Dolls could also be added to
 * regular [Play] levels too.
 *
 * No matter how Dolls are created, the Doll itself is made up of many [DollPart]s, such as a head, torso, arms, legs.
 * The Doll itself isn't visible (only the DollParts are visible).
 * The DollParts are joined together using Tickle's built in JBox2d physics engine.
 */
class Doll : ActionRole(), Reward {

    @Attribute
    override var rewardForScene: String = ""

    @CostumeAttribute
    var defaultScale = 1.0

    var totalMass = 0.0

    val parts = mutableListOf<Actor>()

    val joints = mutableListOf<TicklePinJoint>()

    var ending = false

    /**
     * Used by the [hit] method to ignore some hits.
     */
    var ignoreHitTick = 0L

    override fun createAction(): Action {

        val fades = ParallelAction()
        parts.forEach { part ->
            fades.add(Fade(part.color, 0.5, 0f))
        }

        return Until { ending }
                .then(fades)
                .then(Kill(actor))
    }

    override fun activated() {

        // [hit] will ignore the large change in velocity when the doll is first launched.
        ignoreHitTick = Game.instance.gameLoop.tickCount

        val torso = createPart("torso", 0.0)
        val abdomen = createPart("abdomen", 0.1, torso)

        createPart("head", 0.2, torso)
        createPart("arm-left", -0.1, torso)
        createPart("arm-right", -0.2, torso)
        createPart("leg-left", -0.3, abdomen)
        createPart("leg-right", -0.4, abdomen)

        super.activated()
    }

    fun createPart(part: String, zOrder: Double, joinTo: Actor? = null): Actor {

        val newActor = actor.createChild(part)
        val newRole = newActor.role
        if (newRole is DollPart) {
            newRole.doll = this
            newRole.offset.mul(actor.scale)
            newActor.position.add(newRole.offset)

            if (joinTo != null) {
                val joint = TicklePinJoint(joinTo, newActor, newRole.offset)
                joint.limitRotation(newRole.fromAngle, newRole.toAngle)
                joints.add(joint)
            }
        }
        newActor.zOrder = actor.zOrder + zOrder
        newActor.scaleXY = actor.scaleXY

        totalMass += (newActor.body?.jBox2DBody?.mass ?: 0f)

        parts.add(newActor)
        return newActor
    }

    fun scale(scaleX: Double, scaleY: Double) {
        parts.forEach { part ->
            part.scale.mul(scaleX, scaleY)
        }
        joints.forEach { joint ->
            joint.pointA = joint.pointA.mul(scaleX, scaleY)
        }
        totalMass *= (scaleX * scaleY).toFloat()
    }

    override fun end() {
        super.end()
        parts.forEach { it.die() }
    }

    /**
     * Called from a DollPart when the part has changed velocity (presumably because it has hit another
     * object).
     * Note, the sounds must be added to the [Doll]'s events, not the [DollPart] or [MajorDollPart].
     * i.e. added to Annie, Mike, Fiona, etc.
     */
    fun hit(deltaV: Double) {
        val now = Game.instance.gameLoop.tickCount
        // Has a hit already occurred recently (the last 10 ticks) for this doll? Then ignore the hit.
        // This lets the head, body and torso all call hit, and only the first will cause a sound effect.
        if (now - ignoreHitTick > 10) {
            if (Game.instance.director is Play) {
                actor.event(if (deltaV > 200.0) "hitHard" else "hit")
                ignoreHitTick = now
            }
        }
    }

    fun zapped() {
        AbstractPlay.instance.dolls.remove(this)
        ending = true
    }
}
