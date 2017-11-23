package uk.co.nickthecoder.rapidragdoll

import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.events.Input
import uk.co.nickthecoder.tickle.resources.Resources

class Launcher : AbstractRole() {

    var number: Int = 0

    var select: Input? = null

    var speed: Double = 600.0

    var dollName: String = "annie"

    override fun activated() {
        select = Resources.instance.inputs.find("select${number}")
        if (Play.instance.launcher === this) {
            actor.event("select${number}")
        } else {
            actor.event("deselect${number}")
        }
    }

    override fun tick() {
        if (select?.isPressed() == true) {
            Play.instance.launcher = this
            actor.event("select${number}")
        }
    }

    fun deselect() {
        actor.event("deselect${number}")
    }

    fun launch(aim: Aim) {

        val costume = Resources.instance.costumes.find(dollName)
        costume ?: return

        val dollA = actor.createChild(costume)
        val doll = dollA.role

        if (doll is Doll) {
            doll.initialVelocity.set(aim.actor.position).sub(actor.position).normalize(speed)
            Play.instance.launched(doll)
        }
        actor.stage?.add(dollA)
    }

}
