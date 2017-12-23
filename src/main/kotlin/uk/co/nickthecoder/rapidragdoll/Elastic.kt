package uk.co.nickthecoder.rapidragdoll

import org.jbox2d.dynamics.joints.MouseJoint
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.NinePatchAppearance
import uk.co.nickthecoder.tickle.physics.anchorA
import uk.co.nickthecoder.tickle.physics.anchorB

/**
 * An elastic line between the Hand and the item being dragged.
 * Used on the Victory scenes only.
 */
class Elastic : AbstractRole() {

    var mouseJoint: MouseJoint? = null
        set(v) {
            field = v
            if (v == null) {
                actor.hide()
            } else {
                actor.event("show")
            }
        }

    private val from = Vector2d()

    private val to = Vector2d()

    override fun tick() {
        mouseJoint?.let { mouseJoint ->
            val app = actor.appearance
            if (app is NinePatchAppearance) {
                mouseJoint.anchorA(from)
                mouseJoint.anchorB(to)

                actor.position.set(from)
                app.lineTo(to)
            }
        }
    }

}
