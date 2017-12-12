package uk.co.nickthecoder.rapidragdoll

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Delay
import java.util.*

class Clock : ActionRole() {

    lateinit var minuteHand: Actor
    lateinit var hourHand: Actor


    override fun activated() {
        super.activated()

        minuteHand = actor.createChildOnStage("minute-hand")
        hourHand = actor.createChildOnStage("hour-hand")
        updateHands()
    }

    override fun createAction(): Action {
        return Delay(30.0).then { updateHands() }.forever()

    }

    fun updateHands() {
        val cal = Calendar.getInstance()
        val minutes = cal.get(Calendar.MINUTE)
        val hours = cal.get(Calendar.HOUR)

        minuteHand.direction.degrees = 90.0 - minutes * 6.0
        hourHand.direction.degrees = 90.0 - (hours + minutes / 60.0) * 30.0
    }

}
