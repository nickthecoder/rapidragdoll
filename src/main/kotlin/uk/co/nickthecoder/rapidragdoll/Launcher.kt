package uk.co.nickthecoder.rapidragdoll

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.AttributeType
import uk.co.nickthecoder.tickle.Costume
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Delay
import uk.co.nickthecoder.tickle.events.Input
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.stage.findRoles
import uk.co.nickthecoder.tickle.util.Attribute
import uk.co.nickthecoder.tickle.util.RandomFactory
import uk.co.nickthecoder.tickle.util.item

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

    @Attribute
    var randomSeed = 0

    @Attribute
    var scale = 1.0

    val dollCostumes = mutableListOf<Costume>()

    lateinit var random: RandomFactory

    override fun activated() {
        super.activated()

        random = RandomFactory(randomSeed.toLong())

        dollName.split(",").forEach {
            val name = it.trim()
            val dollCostume = Resources.instance.costumes.find(name)
            if (dollCostume != null) {
                dollCostumes.add(dollCostume)
            }
        }
        if (dollCostumes.isEmpty()) {
            dollCostumes.add(Resources.instance.costumes.find("annie")!!)
        }
    }

    fun launch(point: Vector2d) {
        val world = actor.stage?.world

        if (!clearToLaunch()) {
            return
        }

        val costume = random.item(dollCostumes)
        val dollA = actor.createChild(costume)
        val doll = dollA.role

        if (doll is Doll) {
            dollA.scaleXY = scale * doll.defaultScale

            dollA.zOrder = dollZOrder
            dollZOrder++
            if (dollZOrder > 99) {
                dollZOrder = 1.0
            }

            actor.stage?.add(dollA)

            // Throw the doll by giving ONE body part an initial velocity. This causes it to spin differently
            // depending on which body part is thrown.
            // Don't throw using the legs, because that can cause them to overlap and STICK.
            val partNumber = random.nextInt(doll.parts.size - 2)
            val throwBy = doll.parts[partNumber].body!!
            val direction = Vector2d(point).sub(actor.position)
            val magnitude = Math.min(direction.length(), speed)

            val initialVelocity = direction.normalize(magnitude)
            throwBy.linearVelocity = world?.pixelsToWorld(initialVelocity.mul(doll.totalMass.toDouble() / throwBy.mass))

            AbstractPlay.instance.launched(doll)
        }
    }

    /**
     * Cannot launch if there is a doll close to the launcher
     */
    fun clearToLaunch(): Boolean {
        val distance = Vector2d()
        actor.stage?.findRoles<DollPart>()?.forEach { dollPart ->
            actor.position.sub(dollPart.actor.position, distance)
            if (distance.length() < 100.0) {
                return false
            }
        }
        return true
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
        super.activated()
        select = Resources.instance.inputs.find("select${number}")
        if (AbstractPlay.instance.launcher === this) {
            actor.event("select${number}")
        } else {
            actor.event("deselect${number}")
        }
    }

    override fun tick() {
        if (select?.isPressed() == true) {
            AbstractPlay.instance.launcher = this
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
        super.activated()
        action = Delay(period).then { launch(point) }.forever()
        action.begin()
    }

    override fun tick() {
        action.act()
    }

}
