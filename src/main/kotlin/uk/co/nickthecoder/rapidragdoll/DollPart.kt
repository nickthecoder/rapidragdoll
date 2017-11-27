package uk.co.nickthecoder.rapidragdoll

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.util.Angle
import uk.co.nickthecoder.tickle.util.CostumeAttribute

class DollPart : AbstractRole(), Draggable {

    @CostumeAttribute(order = 1)
    val offset = Vector2d()

    @CostumeAttribute(order = 2)
    val fromAngle = Angle.degrees(-180.0)

    @CostumeAttribute(order = 3)
    val toAngle = Angle.degrees(180.0)

    lateinit var doll: Doll

    override fun tick() {
    }

    override fun mass() = doll.totalMass
}
