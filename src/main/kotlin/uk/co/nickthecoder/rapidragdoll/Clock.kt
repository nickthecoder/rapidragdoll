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
