package uk.co.nickthecoder.rapidragdoll

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.util.CostumeAttribute

class DollPart : AbstractRole() {

    @CostumeAttribute
    val offset = Vector2d()

    override fun tick() {
    }

}
