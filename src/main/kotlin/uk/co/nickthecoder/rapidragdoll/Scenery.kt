package uk.co.nickthecoder.rapidragdoll

import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.util.Attribute

class Scenery : AbstractRole(), Draggable, Reward {

    @Attribute
    override var rewardForScene = ""

    override fun tick() {
    }

}
