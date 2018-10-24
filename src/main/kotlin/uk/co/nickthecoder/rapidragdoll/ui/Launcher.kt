/*
Rapid Rag Doll
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.rapidragdoll.ui

import org.joml.Vector2d
import uk.co.nickthecoder.rapidragdoll.AbstractPlay
import uk.co.nickthecoder.rapidragdoll.roles.Doll
import uk.co.nickthecoder.rapidragdoll.roles.DollPart
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
import uk.co.nickthecoder.tickle.util.listItem

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

    open fun launch(point: Vector2d) {

        if (!clearToLaunch()) {
            return
        }

        val costume = random.listItem(dollCostumes)
        val dollA = costume.createActor()
        val doll = dollA.role

        if (doll is Doll) {
            dollA.position.set(actor.position)
            dollA.scaleXY = scale * doll.defaultScale

            dollA.zOrder = dollZOrder
            dollZOrder++
            if (dollZOrder > 99) {
                dollZOrder = 1.0
            }
            actor.stage?.add(dollA)

            val direction = Vector2d(point).sub(actor.position)
            val magnitude = Math.min(direction.length(), speed) / 1.0
            // Used below to create a rotation of the doll.
            val shear = random.between(-magnitude * 1.0, magnitude * 1.0)

            val initialVelocity = direction.normalize(magnitude)
            val shearedVelocity = Vector2d()
            val headMass = doll.parts[1].body!!.mass

            // Give each doll part an initial velocity, but also cause a rotation (by adding a
            // shear to the head and subtracting it from the abdomen and legs.

            doll.parts.forEachIndexed { index, part ->
                val tickleBody = part.body!!
                if (index == 1) { // The head
                    // Move the head in one direction
                    initialVelocity.add(shear, 0.0, shearedVelocity)
                } else if (index > 3) { // Abdomen and legs
                    // Move the abdomen and legs in the opposite direction,
                    // Causing an overall rotation of the Doll.
                    initialVelocity.sub(shear * headMass / tickleBody.mass / 3, 0.0, shearedVelocity)
                    // If I've got the maths correct, this shouldn't change the direction of the doll, as the
                    // change in momentum is balanced between the head and these 3 parts (abdomen and legs)
                } else {
                    shearedVelocity.set(initialVelocity)
                }
                tickleBody.setLinearVelocity(shearedVelocity)
            }
            launched(doll)

            AbstractPlay.instance.launched(doll)
        }
    }

    open fun launched(doll: Doll) {}

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
        select = Resources.instance.inputs.find("select$number")
        if (AbstractPlay.instance.launcher === this) {
            actor.event("select$number")
        } else {
            actor.event("deselect$number")
        }
    }

    override fun tick() {
        if (select?.isPressed() == true) {
            AbstractPlay.instance.launcher = this
            actor.event("select$number")
            dollCostumes[0].chooseSound("ready")?.play()
        }
    }

    fun deselect() {
        actor.event("deselect$number")
    }

    override fun launched(doll: Doll) {
        doll.actor.event("launched")
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
