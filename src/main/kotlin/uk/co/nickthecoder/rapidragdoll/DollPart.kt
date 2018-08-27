package uk.co.nickthecoder.rapidragdoll

import org.jbox2d.common.Vec2
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.util.Angle
import uk.co.nickthecoder.tickle.util.CostumeAttribute

/**
 * A part of a doll, such as a head, torso, arm or leg.
 * DollParts are created by a [Doll] (which also joins the parts together).
 * Note, a [Doll] is invisible, and only the [DollPart]s are visible.
 */
open class DollPart : AbstractRole(), Draggable {

    @CostumeAttribute(order = 1)
    val offset = Vector2d()

    @CostumeAttribute(order = 2)
    val fromAngle = Angle.degrees(-180.0)

    @CostumeAttribute(order = 3)
    val toAngle = Angle.degrees(180.0)

    lateinit var doll: Doll

    override fun tick() {
        // Does nothing. JBox2d takes care of all movements
    }

    override fun scale(scaleX: Double, scaleY: Double) {
        doll.scale(scaleX, scaleY)
    }

    override fun mass() = doll.totalMass

}

/**
 * The only difference between MajorDollPart and [DollPart] is a test in tick().
 * If the velocity changes a lot, (i.e. the doll has collided with something) then a sound is made.
 * Only the head, torso and abdomen use this class, so no sound when an arm or leg collide.
 * Note, there is a large change in speed when a Doll is launched by a [Launcher]. However,
 * [Doll.hit] ignores this.
 */
class MajorDollPart : DollPart() {

    /**
     * Used to test for then the doll part has hit something
     */
    private var oldSpeed = Vec2(0.0f, 0.0f)

    /**
     * Make a sound when the change in velocity is high (because it has hit something)
     */
    override fun tick() {
        val newSpeed = actor.body?.linearVelocity?.clone() ?: Vec2(0.0f, 0.0f)
        val dx = Math.abs(oldSpeed.x - newSpeed.x)
        val dy = Math.abs(oldSpeed.y - newSpeed.y)

        // 1 pixel per tick is a reasonable change in velocity to initiate a sound.
        if (dx > 1f || dy > 1f) {
            doll.hit(this, dx + dy)
        }
        oldSpeed = newSpeed
    }
}