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
import uk.co.nickthecoder.tickle.physics.pixelsToWorld
import uk.co.nickthecoder.tickle.util.Attribute


class Bell : ActionRole(), Draggable {

    @Attribute
    var ringThreshold = 1f

    @Attribute(attributeType = AttributeType.RELATIVE_POSITION)
    val ceiling = Vector2d()

    lateinit var front: Actor

    lateinit var clanger: Actor

    lateinit var rope: Actor

    var oldAngularVelocity = 0f

    override fun createAction(): Action {

        Play.instance.objectives++

        front = actor.createChildOnStage("front")
        front.scaleXY = actor.scaleXY
        (front.role as Follower).following = actor

        createRope()

        clanger = actor.createChildOnStage("clanger")
        clanger.scaleXY = actor.scaleXY

        val world = actor.body!!.world
        val jointDef = RevoluteJointDef()
        jointDef.bodyA = actor.body
        jointDef.bodyB = clanger.body
        jointDef.collideConnected = true

        jointDef.localAnchorA = pixelsToWorld(Vector2d(0.0, -20.0 * actor.scaleXY))
        world.createJoint(jointDef) as RevoluteJoint

        if (Play.instance is Victory) {
            // Don't disappear on the "Victory" scene
            return Until { hit() }
                    .then {
                        actor.event("ding")
                    }.forever()
        }

        return Until { hit() }
                .then {
                    actor.event("ding")
                    Play.instance.objectives--
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
        rope = actor.createChildOnStage("rope")
        rope.scaleXY = actor.scaleXY

        val world = actor.body!!.world
        val jointDef = RevoluteJointDef()
        jointDef.bodyA = actor.body
        jointDef.bodyB = rope.body

        rope.tiledAppearance?.let { appearance ->
            appearance.size.y = ceiling.y / actor.scaleXY
        }
        world.createJoint(jointDef) as RevoluteJoint

    }

    fun hit(): Boolean {
        val diff = Math.abs(clanger.body!!.angularVelocity - oldAngularVelocity)
        oldAngularVelocity = clanger.body!!.angularVelocity
        return diff > ringThreshold
    }

}
