package uk.co.nickthecoder.rapidragdoll

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.AbstractRole

class Arrow : AbstractRole() {

    private val mouse = Vector2d()
    private val tempVector = Vector2d()

    override fun tick() {
        actor.stage?.firstView()?.mousePosition(mouse)
        AbstractPlay.instance.launcher?.let {
            actor.position.set(it.actor.position)
            mouse.sub(actor.position, tempVector)
            val magnitude = tempVector.length()

            if (magnitude > it.speed) {
                // Show the Aim actor, and draw the arrow from the launcher towards the mouse position
                // With magnitude of the maximum speed of the launcher.
                AbstractPlay.instance.aim?.actor?.event("default")
                tempVector.normalize(it.speed).add(actor.position)
                actor.ninePatchAppearance?.lineTo(tempVector)
            } else {
                // Hide the Aim actor, and draw the arrow from the launcher to the mouse position
                AbstractPlay.instance.aim?.actor?.hide()
                actor.ninePatchAppearance?.lineTo(mouse)
            }
        }
    }

}
