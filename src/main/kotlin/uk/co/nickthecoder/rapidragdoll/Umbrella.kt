package uk.co.nickthecoder.rapidragdoll

import org.jbox2d.dynamics.joints.RevoluteJointDef
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.physics.pixelsToWorld
import uk.co.nickthecoder.tickle.util.Angle
import uk.co.nickthecoder.tickle.util.Attribute

class Umbrella : AbstractRole() {

    @Attribute
    val angle = Angle()

    val joinPoint = Vector2d(0.0, 141.0)

    override fun activated() {
        val topHalf = actor.createChildOnStage("topHalf")
        topHalf.position.y += joinPoint.y
        topHalf.updateBody()

        actor.body?.let { joinTo ->

            val jointDef = RevoluteJointDef()
            jointDef.bodyA = joinTo
            jointDef.bodyB = topHalf.body

            jointDef.localAnchorA = pixelsToWorld(joinPoint)
            jointDef.lowerAngle = Math.toRadians(-60.0).toFloat()
            jointDef.upperAngle = Math.toRadians(60.0).toFloat()
            jointDef.enableLimit = true

            Game.instance.scene.world?.createJoint(jointDef)
        }
        topHalf.direction.radians += angle.radians
        topHalf.updateBody()
    }

    override fun tick() {
    }
}