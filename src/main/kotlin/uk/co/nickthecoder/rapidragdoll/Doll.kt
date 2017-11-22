package uk.co.nickthecoder.rapidragdoll

import org.jbox2d.dynamics.joints.RevoluteJointDef
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.AttributeType
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.physics.pixelsToWorld
import uk.co.nickthecoder.tickle.util.Attribute

class Doll : AbstractRole() {

    @Attribute(attributeType = AttributeType.RELATIVE_POSITION)
    val initialVelocity = Vector2d()

    override fun activated() {
        val torso = createPart("torso", 0)
        val head = createPart("head", 1, torso)
        val leftArm = createPart("arm-left", -1, torso)
        val rightArm = createPart("arm-right", -1, torso)
        val abdomen = createPart("abdomen", 1, torso)
        val leftLeg = createPart("leg-left", -1, abdomen)
        val rightLeg = createPart("leg-right", -1, abdomen)
        actor.die()
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

                val joint = Game.instance.scene.world?.createJoint(jointDef)
            }
        }
        newActor.body?.linearVelocity = pixelsToWorld(initialVelocity)
        newActor.zOrder = actor.zOrder + zOrder

        return newActor
    }

    override fun tick() {
    }

}
