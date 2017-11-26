package uk.co.nickthecoder.rapidragdoll

import org.jbox2d.dynamics.joints.RevoluteJointDef
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Kill
import uk.co.nickthecoder.tickle.action.ParallelAction
import uk.co.nickthecoder.tickle.action.Until
import uk.co.nickthecoder.tickle.action.animation.Fade
import uk.co.nickthecoder.tickle.physics.pixelsToWorld
import java.util.*

private val random = Random()

class Doll : ActionRole() {

    val initialVelocity = Vector2d()

    var totalMass = 0.0

    val parts = mutableListOf<Actor>()

    var ending = false

    override fun createAction(): Action {

        val torso = createPart("torso", 0.0)
        val abdomen = createPart("abdomen", 0.1, torso)

        createPart("head", 0.1, torso)
        createPart("arm-left", -0.1, torso)
        createPart("arm-right", -0.1, torso)
        createPart("leg-left", -0.1, abdomen)
        createPart("leg-right", -0.1, abdomen)

        // Throw the doll by giving ONE body part an initial velocity. This causes it to spin differently
        // depending on which body part is thrown.
        val throwBy = parts[random.nextInt(parts.size)].body!!
        throwBy.linearVelocity = pixelsToWorld(initialVelocity.mul(totalMass / throwBy.mass))

        val fades = ParallelAction()
        parts.forEach { part ->
            fades.add(Fade(part.color, 0.5, 0f))
        }

        return Until { ending }
                .then(fades)
                .then(Kill(actor))
    }

    fun createPart(part: String, zOrder: Double, joinTo: Actor? = null): Actor {
        val newActor = actor.createChildOnStage(part)
        val newRole = newActor.role
        if (newRole is DollPart) {
            newActor.position.add(newRole.offset)
            newActor.updateBody()

            if (joinTo != null) {

                val jointDef = RevoluteJointDef()
                jointDef.bodyA = joinTo.body
                jointDef.bodyB = newActor.body

                jointDef.localAnchorA = pixelsToWorld(newRole.offset)
                jointDef.lowerAngle = newRole.fromAngle.radians.toFloat()
                jointDef.upperAngle = newRole.toAngle.radians.toFloat()
                jointDef.enableLimit = true

                Game.instance.scene.world?.createJoint(jointDef)
            }
        }
        newActor.zOrder = actor.zOrder + zOrder

        totalMass += newActor.body?.mass ?: 0f

        parts.add(newActor)
        return newActor
    }


    override fun end() {
        super.end()
        parts.forEach { it.die() }
    }
}
