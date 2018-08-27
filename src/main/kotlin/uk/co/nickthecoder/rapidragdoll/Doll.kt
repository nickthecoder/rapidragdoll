package uk.co.nickthecoder.rapidragdoll

import org.jbox2d.dynamics.joints.RevoluteJoint
import org.jbox2d.dynamics.joints.RevoluteJointDef
import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Kill
import uk.co.nickthecoder.tickle.action.ParallelAction
import uk.co.nickthecoder.tickle.action.Until
import uk.co.nickthecoder.tickle.action.animation.Fade
import uk.co.nickthecoder.tickle.physics.mul
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

    var totalMass = 0f

    val parts = mutableListOf<Actor>()

    val joints = mutableListOf<RevoluteJoint>()

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
        val world = actor.stage?.world
        val newActor = actor.createChildOnStage(part)
        val newRole = newActor.role
        if (newRole is DollPart) {
            newRole.doll = this
            newRole.offset.mul(actor.scale)
            newActor.position.add(newRole.offset)

            if (joinTo != null) {
                val jointDef = RevoluteJointDef()
                jointDef.bodyA = joinTo.body
                jointDef.bodyB = newActor.body

                jointDef.localAnchorA = world?.pixelsToWorld(newRole.offset)
                jointDef.lowerAngle = newRole.fromAngle.radians.toFloat()
                jointDef.upperAngle = newRole.toAngle.radians.toFloat()
                jointDef.enableLimit = true
                jointDef.collideConnected = false
                val joint = world?.createJoint(jointDef) as RevoluteJoint
                joints.add(joint)
            }
        }
        newActor.zOrder = actor.zOrder + zOrder
        newActor.scaleXY = actor.scaleXY

        totalMass += (newActor.body?.mass ?: 0f)

        parts.add(newActor)
        return newActor
    }

    fun scale(scaleX: Double, scaleY: Double) {
        val world = actor.stage?.world

        parts.forEach { part ->
            part.scale.mul(scaleX, scaleY)
        }
        val newJoints = mutableListOf<RevoluteJoint>()
        joints.forEach { joint ->

            val jointDef = RevoluteJointDef()
            jointDef.bodyA = joint.bodyA
            jointDef.bodyB = joint.bodyB

            jointDef.localAnchorA = joint.m_localAnchor1.mul(scaleX.toFloat(), scaleY.toFloat())

            jointDef.lowerAngle = joint.m_lowerAngle
            jointDef.upperAngle = joint.m_upperAngle
            jointDef.enableLimit = true
            jointDef.collideConnected = false

            world?.destroyJoint(joint)
            val newJoint = world?.createJoint(jointDef) as RevoluteJoint
            newJoints.add(newJoint)
        }

        joints.clear()
        joints.addAll(newJoints)
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
    fun hit(dollPart: DollPart, deltaV: Float) {
        val now = Game.instance.gameLoop.tickCount
        // Has a hit already occurred recently (the last 10 ticks) for this doll? Then ignore the hit.
        // This lets the head, body and torso all call hit, and only the first will cause a sound effect.
        if (now - ignoreHitTick > 10) {
            if (Game.instance.director is Play) {
                actor.event(if (deltaV > 2.0f) "hitHard" else "hit")
                ignoreHitTick = now
                println("Hit $now, $deltaV")
            }
        }
    }

    fun zapped() {
        AbstractPlay.instance.dolls.remove(this)
        ending = true
    }
}
