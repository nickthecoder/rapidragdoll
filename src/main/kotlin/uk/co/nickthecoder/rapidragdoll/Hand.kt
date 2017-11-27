package uk.co.nickthecoder.rapidragdoll

import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.stage.StageView

/**
 * Used in the victory scene as the mouse pointer.
 */
class Hand : AbstractRole() {

    var view: StageView? = null

    override fun activated() {
        super.activated()
        view = actor.stage?.firstView()
    }

    override fun tick() {
        view?.mousePosition(actor.position)
        actor.updateBody()
    }

}
