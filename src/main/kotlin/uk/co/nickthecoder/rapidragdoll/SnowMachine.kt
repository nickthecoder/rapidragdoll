package uk.co.nickthecoder.rapidragdoll

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Delay
import uk.co.nickthecoder.tickle.util.Attribute
import uk.co.nickthecoder.tickle.util.Rand

class SnowMachine : ActionRole() {

    @Attribute
    var toX = 1280.0

    @Attribute
    var period = 1.0

    @Attribute
    var count = 1000

    override fun createAction(): Action {
        return Delay(period)
                .then {
                    val snowA = actor.createChildOnStage("snow")
                    snowA.x = Rand.between(actor.x, toX)
                    snowA.y = actor.y
                }.repeat(count)
    }

}
