package uk.co.nickthecoder.rapidragdoll

import org.jbox2d.dynamics.joints.RevoluteJointDef
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.physics.pixelsToWorld
import java.util.*

private val random = Random()

class Doll : AbstractRole() {

    val initialVelocity = Vector2d()

    var totalMass = 0.0

    val parts = mutableListOf<Actor>()

    override fun activated() {

        val torso = createPart("torso", 0)
        val abdomen = createPart("abdomen", 1, torso)

        createPart("head", 1, torso)
        createPart("arm-left", -1, torso)
        createPart("arm-right", -1, torso)
        createPart("leg-left", -1, abdomen)
        createPart("leg-right", -1, abdomen)

        // Throw the doll by giving ONE body part an initial velocity. This causes it to spin differently
        // depending on which body part is thrown.
        val part = parts[random.nextInt(parts.size)]
        val partBody = part.body!!
        partBody.linearVelocity = pixelsToWorld(initialVelocity.mul(totalMass / partBody.mass))
    }

    fun createPart(part: String, zOrder: Int, joinTo: Actor? = null): Actor {
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

                val joint = Game.instance.scene.world?.createJoint(jointDef)
            }
        }
        newActor.zOrder = actor.zOrder + zOrder

        totalMass += newActor.body?.mass ?: 0f

        parts.add(newActor)
        return newActor
    }

    override fun tick() {
    }

    override fun end() {
        super.end()
        parts.forEach { it.die() }
    }
}
