package uk.co.nickthecoder.rapidragdoll

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.AttributeType
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Delay
import uk.co.nickthecoder.tickle.events.Input
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.util.Attribute


/**
 * Each doll is given a zOrder incremented by 1, and then resets to back to 1 when it reaches 100.
 * This ensures that each doll is either "above" or "below" other dolls.
 * Note that each DollPart has a zOrder difference in the range -0.1 to 0.1, so that
 * each doll part is ordered correctly. If a scene allows more than 100 dolls, then is it possible for
 * dolls to overlap strangely. Not a big deal though!
 *
 * All objects that must be draw on TOP of the dolls must have zOrder > 100 and those below the dolls
 * must have zOrder < 0.
 *
 * Note, this is a global, so that all Launchers use the same counter.
 */
private var dollZOrder = 1.0

abstract class AbstractLauncher : AbstractRole() {

    @Attribute
    var speed: Double = 600.0

    @Attribute
    var dollName: String = "annie"

    fun launch(point: Vector2d) {

        val costume = Resources.instance.costumes.find(dollName)
        costume ?: return

        val dollA = actor.createChild(costume)
        dollA.zOrder = dollZOrder
        dollZOrder++
        if (dollZOrder > 99) {
            dollZOrder = 1.0
        }

        val doll = dollA.role

        if (doll is Doll) {
            doll.initialVelocity.set(point).sub(actor.position).normalize(speed)
            Play.instance.launched(doll)
        }
        actor.stage?.add(dollA)
    }

}

class Launcher : AbstractLauncher() {

    @Attribute(attributeType = AttributeType.ABSOLUTE_POSITION)
    val panTo = Vector2d()

    /**
     * The number of this launcher. Set by Play director when the scene begins.
     */
    var number: Int = 0

    /**
     * The key to select this Launcher (Will be key 1..n)
     */
    var select: Input? = null

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

}

class AutoLauncher : AbstractLauncher() {

    @Attribute
    var period = 2.0

    @Attribute(attributeType = AttributeType.ABSOLUTE_POSITION)
    val point = Vector2d()

    lateinit var action: Action

    override fun activated() {
        action = Delay(period).then { launch(point) }.forever()
        action.begin()
    }

    override fun tick() {
        action.act()
    }

}
